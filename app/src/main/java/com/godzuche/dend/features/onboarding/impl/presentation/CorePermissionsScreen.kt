package com.godzuche.dend.features.onboarding.impl.presentation

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godzuche.dend.R
import com.godzuche.dend.app.CALL_SCREENING_PERMISSIONS
import com.godzuche.dend.app.checkPermissions
import com.godzuche.dend.designsystem.theme.DendTheme
import com.godzuche.dend.features.onboarding.impl.components.PermissionChecklistItem

@Composable
fun CorePermissionsScreen(
    onboardingViewModel: OnboardingViewModel,
) {
    val uiState by onboardingViewModel.uiState.collectAsState()

    CorePermissionsScreenContent(
        state = uiState,
        onPermissionResult = onboardingViewModel::onSinglePermissionResult,
        onFinishClicked = onboardingViewModel::onFinishClicked,
        onCheckPermissionsGranted = onboardingViewModel::onCheckPermissionsGranted,
    )

}

@Composable
fun CorePermissionsScreenContent(
    state: CorePermissionsUiState,
    modifier: Modifier = Modifier,
    onPermissionResult: (String, Boolean) -> Unit,
    onFinishClicked: () -> Unit,
    onCheckPermissionsGranted: (Map<String, Boolean>) -> Unit,
) {
    val context = LocalContext.current

    val launchers = mapOf(
        Manifest.permission.ANSWER_PHONE_CALLS to rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onPermissionResult(
                Manifest.permission.ANSWER_PHONE_CALLS,
                isGranted,
            )
        },
        Manifest.permission.READ_CONTACTS to rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onPermissionResult(
                Manifest.permission.READ_CONTACTS,
                isGranted,
            )
        },
        /*Manifest.permission.READ_CALL_LOG*/
        CALL_HISTORY_PERMISSIONS to rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            // When READ_CALL_LOG is granted, we also grant our conceptual WRITE_CALL_LOG
            // since they are requested together.
            onPermissionResult(
                CALL_HISTORY_PERMISSIONS,
                results.all { it.value },
            )
        },
    )

    LaunchedEffect(Unit) {
        val checkPermissionsResults = context.checkPermissions(CALL_SCREENING_PERMISSIONS)
        onCheckPermissionsGranted(checkPermissionsResults)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
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
                        item = item, onEnableClick = { permission ->
                            when (permission == CALL_HISTORY_PERMISSIONS) {
                                true -> {
                                    (launchers[permission] as? ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>)?.launch(
                                        arrayOf(
                                            Manifest.permission.READ_CALL_LOG,
                                            Manifest.permission.WRITE_CALL_LOG,
                                        )
                                    )
                                }

                                else -> {
                                    (launchers[permission] as? ManagedActivityResultLauncher<String, Boolean>)?.launch(
                                        permission
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
                onClick = onFinishClicked,
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
    )
}
