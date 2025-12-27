package com.godzuche.dend.features.main.impl.presentation.components

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.godzuche.dend.R
import com.godzuche.dend.core.data.utils.resolveContactUri
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.features.rules.impl.presentation.RulesViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

private enum class PermissionRequest {
    CONTACTS,
    CALL_LOG,
}

@Composable
fun AddNumberSheet(
    onDismiss: () -> Unit,
    onAddFromRecentsClick: () -> Unit,
    rulesViewModel: RulesViewModel = koinActivityViewModel(),
) {
    val context = LocalContext.current

    var permissionRequest by remember { mutableStateOf<PermissionRequest?>(null) }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contactUri: Uri? = result.data?.data
            contactUri?.let {
                val contactDetails = context.resolveContactUri(contactUri)

                if (contactDetails != null) {
                    Log.d(
                        "RulesScreen",
                        "Selected Contact: Name=${contactDetails.name}, Number=${contactDetails.phoneNumber}"
                    )

                    rulesViewModel.addRule(
                        number = contactDetails.phoneNumber,
                        name = contactDetails.name,
                    )
                } else {
                    Log.w("RulesScreen", "Failed to resolve contact details from URI.")
                }
            }

            onDismiss()
        } else {
            println("Picked contact failed")
        }

    }

//    val callLogLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) {
//        // TODO: Handle result from our custom call log screen
//        onDismiss()
//    }

    val readContactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionRequest = null // Hide dialog
        if (isGranted) {
            contactPickerLauncher.launch(
                Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                )
            )
        } else {
            // TODO: Show a message that the feature is unavailable (e.g., with a Snackbar)
        }
    }

    val readCallLogPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionRequest = null // Hide dialog
        if (permissions[Manifest.permission.READ_CALL_LOG] == true) {
            // As a placeholder, launch the system call log viewer. A real app might open a custom screen.
//            callLogLauncher.launch(
//                Intent(
//                    Intent.ACTION_VIEW,
//                    android.provider.CallLog.Calls.CONTENT_URI
//                )
//            )
            onAddFromRecentsClick()
        } else {
            // TODO: Show a message that the feature is unavailable
        }
    }

    AddNumberSheetContent(
        onAddFromContactsClick = {
            when (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)) {
                PackageManager.PERMISSION_GRANTED -> {
                    contactPickerLauncher.launch(
                        Intent(
                            Intent.ACTION_PICK,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                        )
                    )
                }

                else -> {
                    permissionRequest = PermissionRequest.CONTACTS
                }
            }
        },
        onAddFromRecentsClick = {
            when (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)) {
                PackageManager.PERMISSION_GRANTED -> {
//                    callLogLauncher.launch(
//                        Intent(
//                            Intent.ACTION_VIEW,
//                            android.provider.CallLog.Calls.CONTENT_URI
//                        )
//                    )
                    onAddFromRecentsClick()
                }

                else -> {
                    permissionRequest = PermissionRequest.CALL_LOG
                }
            }
        },
        onAddManuallyClick = {
            onDismiss()
            rulesViewModel.setShowAddManuallyDialogState(true)
        },
    )

    permissionRequest?.let { request ->
        PermissionPrimerDialog(
            permissionRequest = request,
            onDismiss = { permissionRequest = null },
            onConfirm = {
                when (request) {
                    PermissionRequest.CONTACTS -> readContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    PermissionRequest.CALL_LOG -> readCallLogPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.WRITE_CALL_LOG,
                        )
                    )
                }
            }
        )
    }

}

@Composable
fun AddNumberSheetContent(
    onAddFromContactsClick: () -> Unit,
    onAddFromRecentsClick: () -> Unit,
    onAddManuallyClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
//            .clip(RoundedCornerShape(48.dp))
            .clip(shape = RoundedCornerShape(16.dp)),
    ) {
        Text(
            text = "Add a Number",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp),
        )

        RuleActionItem(
            icon = ImageVector.vectorResource(R.drawable.contacts_24dp),
            label = "Add from Contacts",
            onClick = onAddFromContactsClick,
        )
        RuleActionItem(
            icon = ImageVector.vectorResource(R.drawable.history_2_24dp),
            label = "Add from Recent Calls",
            onClick = onAddFromRecentsClick,
        )
        RuleActionItem(
            icon = ImageVector.vectorResource(R.drawable.dialpad_24dp),
            label = "Enter Manually",
            onClick = onAddManuallyClick,
        )
    }
}


/**
 * A reusable composable for each tappable row inside the bottom sheet.
 *
 * @param icon The icon to display on the left.
 * @param label The text label for the action.
 * @param onClick The lambda to be executed when the item is clicked.
 */
@Composable
private fun RuleActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // The label describes the action
            tint = MaterialTheme.colorScheme.primary,
        )

        Spacer(Modifier.width(20.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

/**
 * A dialog to explain why a permission is needed before showing the system dialog.
 */
@Composable
private fun PermissionPrimerDialog(
    permissionRequest: PermissionRequest,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val title: String
    val text: String

    when (permissionRequest) {
        PermissionRequest.CONTACTS -> {
            title = "Allow Contact Access?"
            text =
                "To let you choose a number directly from your contacts, DeeNDee needs permission to read them. Your contacts are not stored or uploaded."
        }

        PermissionRequest.CALL_LOG -> {
            title = "Allow Call Log Access?"
            text =
                "To let you add a number from your recent calls and to keep your call history clean, DeeNDee needs access to your call log."
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Allow")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(title) },
        text = { Text(text) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun AddNumberSheetPreview() = DendTheme {

    AddNumberSheet({}, {})

}
