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
fun RoleSettingsDialog(
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onGoToSettings) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.admin_panel_settings_24dp),
                contentDescription = null,
            )
        },
        title = { Text("Permission Required") },
        text = {
            Text(
                "To work as a call firewall, DenD must be set as the 'Caller ID & spam app'.\n\n" +
                        "Since the dialog may have been disabled/dismissed, you may need to set this manually in your phone's settings."
            )
        }
    )
}