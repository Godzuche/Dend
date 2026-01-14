package com.godzuche.dend.features.onboarding.impl.presentation

import android.Manifest
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.godzuche.dend.R
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.core.presentation.utils.checkPermissions
import com.godzuche.dend.core.presentation.utils.isPermissionPermanentlyDeclined
import com.godzuche.dend.core.presentation.utils.openAppSettings
import com.godzuche.dend.core.services.callscreening.CALL_SCREENING_PERMISSIONS
import com.godzuche.dend.core.services.callscreening.bindMyService
import com.godzuche.dend.features.onboarding.impl.presentation.components.PermanentlyDeniedDialog
import com.godzuche.dend.features.onboarding.impl.presentation.components.PermissionChecklistItem
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun CorePermissionsScreen() {
    val onboardingViewModel = koinActivityViewModel<OnboardingViewModel>()
    val uiState by onboardingViewModel.uiState.collectAsState()

    CorePermissionsScreenContent(
        state = uiState,
        onPermissionResult = onboardingViewModel::onSinglePermissionResult,
        onFinishClicked = onboardingViewModel::onFinishClicked,
        onCheckPermissionsGranted = onboardingViewModel::onCheckPermissionsGranted,
        visiblePermissionDialogQueue = onboardingViewModel.visiblePermissionDialogQueue,
        onDismissPermissionDialog = onboardingViewModel::dismissPermissionDialog,
    )
}

@Composable
fun CorePermissionsScreenContent(
    state: CorePermissionsUiState,
    visiblePermissionDialogQueue: SnapshotStateList<PermissionItem>,
    modifier: Modifier = Modifier,
    onPermissionResult: (String, Boolean) -> Unit,
    onFinishClicked: () -> Unit,
    onCheckPermissionsGranted: (Map<String, Boolean>) -> Unit,
    onDismissPermissionDialog: () -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    val multiplePermissionsResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val callHistoryPermissions = CALL_HISTORY_PERMISSIONS.split(",")
        val callHistoryPermissionsResult = perms.filterKeys { it in callHistoryPermissions }
            .ifEmpty { null }

        callHistoryPermissionsResult?.let {
            val isGranted = it.all { it.value }
            onPermissionResult(
                CALL_HISTORY_PERMISSIONS,
                isGranted,
            )
        }

        perms.filterKeys { it !in callHistoryPermissions }
            .forEach {
                onPermissionResult(
                    it.key,
                    it.value,
                )
            }
    }

    visiblePermissionDialogQueue
        .reversed()
        .forEach { permissionItem ->
            PermanentlyDeniedDialog(
                permissionName = permissionItem.title,
                featureDescription = permissionItem.subtitle.replaceFirst(
                    "Allows the app to ",
                    "to "
                ),
                onDismiss = {
                    onDismissPermissionDialog()
                },
                isPermanentlyDeclined = activity?.isPermissionPermanentlyDeclined(permissionItem.permission)
                    ?: false,
                onGoToSettings = {
                    onDismissPermissionDialog()
                    context.openAppSettings()
                },
                onRequestPermission = {
                    onDismissPermissionDialog()
                    when (permissionItem.permission == CALL_HISTORY_PERMISSIONS) {
                        true -> {
                            multiplePermissionsResultLauncher.launch(
                                arrayOf(
                                    Manifest.permission.READ_CALL_LOG,
                                    Manifest.permission.WRITE_CALL_LOG,
                                )
                            )
                        }

                        else -> {
                            multiplePermissionsResultLauncher.launch(
                                arrayOf(permissionItem.permission)
                            )
                        }
                    }
                }
            )
        }

    LifecycleStartEffect(Unit) {
        val checkPermissionsResults = context.checkPermissions(CALL_SCREENING_PERMISSIONS)
        onCheckPermissionsGranted(checkPermissionsResults)

        onStopOrDispose { }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .align(alignment = Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(top = 16.dp, bottom = 136.dp),
            ) {
                item {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.admin_panel_settings_24dp),
                        contentDescription = "Permission Icon",
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(Modifier.height(32.dp))

                    Text(
                        "Enable Core Features",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Grant these permissions one by one to unlock DenD's full potential.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(Modifier.height(32.dp))
                }

                itemsIndexed(
                    items = state.permissions,
                    contentType = { index: Int, item: PermissionItem -> "Permission" },
                    key = { index: Int, item: PermissionItem -> item.permission },
                ) { index, item ->
                    PermissionChecklistItem(
                        item = item,
                        onEnableClick = { permission ->
                            println("UI Click permission: $permission")
                            when (permission == CALL_HISTORY_PERMISSIONS) {
                                true -> {
                                    multiplePermissionsResultLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.READ_CALL_LOG,
                                            Manifest.permission.WRITE_CALL_LOG,
                                        )
                                    )
                                }

                                else -> {
                                    multiplePermissionsResultLauncher.launch(
                                        arrayOf(permission)
                                    )
                                }
                            }
                        }
                    )

                    if (index < state.permissions.lastIndex) {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            ElevatedButton(
                onClick = {
                    context.applicationContext.bindMyService()
                    onFinishClicked()
                },
                enabled = state.isFinishButtonEnabled,
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .align(alignment = Alignment.BottomCenter),
            ) {
                Text("Finish Setup")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CorePermissionsScreenPreview() = DendTheme {
    CorePermissionsScreenContent(
        state = CorePermissionsUiState(),
        onPermissionResult = { _, _ -> },
        onFinishClicked = {},
        onCheckPermissionsGranted = {},
        visiblePermissionDialogQueue = remember { mutableStateListOf() },
        onDismissPermissionDialog = {},
    )
}
