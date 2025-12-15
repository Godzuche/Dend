package com.godzuche.dend.features.onboarding.impl.presentation

import android.Manifest
import androidx.compose.runtime.Stable

data class CorePermissionsUiState(
    val permissions: List<PermissionItem> = listOf(
        PermissionItem(
            permission = Manifest.permission.ANSWER_PHONE_CALLS,
            title = "Hang Up on Calls",
            subtitle = "Allows the app to actively reject unwanted calls.",
        ),
        PermissionItem(
            permission = Manifest.permission.READ_CONTACTS,
            title = "Identify Your Contacts",
            subtitle = "Needed to show names and avoid blocking people you know.",
        ),
        PermissionItem(
//            permission = Manifest.permission.READ_CALL_LOG,
            permission = CALL_HISTORY_PERMISSIONS,
            title = "Manage Call History",
            subtitle = "To find recent callers & keep your history clean.",
        )
    )
) {
    val isFinishButtonEnabled: Boolean
        get() = permissions.all { it.isGranted }
}

@Stable
data class PermissionItem(
    val permission: String,
    val title: String,
    val subtitle: String,
    var isGranted: Boolean = false,
)

val CALL_HISTORY_PERMISSIONS = listOf(
    Manifest.permission.READ_CALL_LOG,
    Manifest.permission.WRITE_CALL_LOG,
).joinToString(separator = ",")
