package com.godzuche.dend.features.onboarding.impl.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.godzuche.dend.R

@Composable
fun PermanentlyDeniedDialog(
    permissionName: String,
    featureDescription: String,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit,
    onRequestPermission: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = if (isPermanentlyDeclined) {
                    onGoToSettings
                } else onRequestPermission
            ) {
                Text(
                    text = if (isPermanentlyDeclined) {
                        "Open Settings"
                    } else "Okay"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.block_24dp),
                contentDescription = "Permission Denied Icon",
            )
        },
        title = { Text("$permissionName Permission Denied") },
        text = {
            Text(
                text = if (isPermanentlyDeclined) {
                    "You have permanently denied the $permissionName permission. " +
                            "DenD needs this permission $featureDescription.\n\n" +
                            "To enable this feature, you must grant the permission manually in the app settings."
                } else {
                    "DenD app needs access to $permissionName for proper functioning"
                }
            )
        }
    )
}