package com.godzuche.dend.features.onboarding.impl.presentation

import android.Manifest
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            // Note: We can bundle WRITE_CALL_LOG with this request
        )
    )
) {
    val isFinishButtonEnabled: Boolean
        get() = permissions.all { it.isGranted }
}

//sealed class PermissionItem2(
//    val permission: String,
//    val title: String,
//    val subtitle: String,
//    var isGranted: Boolean = false,
//) {
//    data class AnswerPhoneCalls(): PermissionItem2
//}

class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CorePermissionsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<OnboardingEvent>()
    val events = _events.asSharedFlow()

    fun onGrantRolePermissionClicked() {
        viewModelScope.launch {
            _events.emit(OnboardingEvent.RequestRolePermission)
        }
    }

    fun onRolePermissionResult(isGranted: Boolean) {
        viewModelScope.launch {
            if (isGranted) {
                _events.emit(OnboardingEvent.NavigateToCorePermissions)
            } else {
                // The user denied the permission.
                _events.emit(OnboardingEvent.ShowPermissionDeniedMessage)
            }
        }
    }

//    fun onGrantCorePermissionsClicked() {
//        viewModelScope.launch {
//            _events.emit(OnboardingEvent.RequestCorePermissions)
//        }
//    }
//
//    fun onCorePermissionsResult(results: Map<String, Boolean>) {
//        viewModelScope.launch {
//            // A simple check to see if all values in the map are 'true'.
//            // A more robust implementation could check each one and explain why it's needed if denied.
//            if (results.all { it.value }) {
//                // TODO: Perform any first-time setup here if needed.
//                _events.emit(OnboardingEvent.OnboardingSuccess)
//            } else {
//                // Handle partial or full denial of core permissions
//                _events.emit(OnboardingEvent.ShowPermissionDeniedMessage)
//            }
//        }
//    }

//    fun onPermissionsResult(results: Map<String, Boolean>) {
//        _uiState.update { currentState ->
//            val updatedPermissions = currentState.permissions.map { permissionItem ->
//                permissionItem.copy(isGranted = results[permissionItem.permission] == true)
//            }
//            currentState.copy(permissions = updatedPermissions)
//        }
//    }

    fun onCheckPermissionsGranted(results: Map<String, Boolean>) {
        _uiState.update { currentState ->
            val updatedPermissions = currentState.permissions.map {
                val isPermissionGranted = if (it.permission == CALL_HISTORY_PERMISSIONS) {
                    val callHistoryPermissions = CALL_HISTORY_PERMISSIONS.split(",")
                    results
                        .filterKeys { it in (callHistoryPermissions) }
                        .ifEmpty { null }
                        ?.all { it.value }
                        ?: it.isGranted
                } else {
                    results[it.permission] ?: it.isGranted
                }

                it.copy(isGranted = isPermissionGranted)
            }

            currentState.copy(
                permissions = updatedPermissions,
            )
        }
    }

    fun onSinglePermissionResult(permission: String, isGranted: Boolean) {
        if (!isGranted) return // For now, do nothing if the user denies.

        _uiState.update { currentState ->
            val updatedPermissions = currentState.permissions.map {
                if (it.permission == permission) {
                    it.copy(isGranted = true)
                } else {
                    it
                }
            }
            currentState.copy(permissions = updatedPermissions)
        }
    }

//    fun onCallLogPermissionsResult(results: Map<String, Boolean>) {
//        // Check if BOTH permissions in the group were granted.
//        val readGranted = results[Manifest.permission.READ_CALL_LOG] ?: false
//        val writeGranted = results[Manifest.permission.WRITE_CALL_LOG] ?: false
//
//        if (readGranted && writeGranted) {
//            // If both are granted, update the state for the corresponding UI item.
//            _uiState.update { currentState ->
//                val updatedPermissions = currentState.permissions.map {
//                    if (it.permission == Manifest.permission.READ_CALL_LOG) {
//                        it.copy(isGranted = true)
//                    } else {
//                        it
//                    }
//                }
//                currentState.copy(permissions = updatedPermissions)
//            }
//        } else {
//            // Handle the case where one or both were denied.
//            // For now, we do nothing, but you could show a specific error message.
//        }
//    }

    fun onFinishClicked() {
        if (uiState.value.isFinishButtonEnabled) {
            // TODO: Perform any first-time setup here if needed.
            viewModelScope.launch { _events.emit(OnboardingEvent.OnboardingSuccess) }
        }
    }

}
