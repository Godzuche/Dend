package com.godzuche.dend.features.onboarding.impl.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.dend.core.domain.repository.UserDataRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CorePermissionsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<OnboardingEvent>()
    val events = _events.asSharedFlow()

    val visiblePermissionDialogQueue = mutableStateListOf<PermissionItem>()

    fun dismissPermissionDialog() {
        visiblePermissionDialogQueue.removeAt(0)
    }

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
//                _events.emit(OnboardingEvent.ShowPermissionDeniedMessage("ROLE_CALL_SCREENING"))
                _events.emit(OnboardingEvent.ShowManualRoleSettingsGuidance)
            }
        }
    }

    fun onCheckPermissionsGranted(results: Map<String, Boolean>) {
        _uiState.update { currentState ->
            val updatedPermissions = currentState.permissions.map {
                val isPermissionGranted = if (it.permission == CALL_HISTORY_PERMISSIONS) {
                    val callHistoryPermissions = CALL_HISTORY_PERMISSIONS.split(",")
                    results
                        .filterKeys { it in callHistoryPermissions }
                        .ifEmpty { null }
                        ?.all { it.value }
                        ?: it.isGranted
                } else {
                    results[it.permission] ?: it.isGranted
                }

                it.copy(isGranted = isPermissionGranted)
            }

            currentState.copy(
                permissions = updatedPermissions.toPersistentList(),
            )
        }
    }

    fun onSinglePermissionResult(permission: String, isGranted: Boolean) {
        if (!isGranted) {
            val callHistoryPermissions = CALL_HISTORY_PERMISSIONS.split(",")

            val modifiedPermission = if (permission in callHistoryPermissions) {
                CALL_HISTORY_PERMISSIONS
            } else permission

            visiblePermissionDialogQueue.add(
                uiState.value.permissions.firstOrNull { it.permission == modifiedPermission }
                    ?: throw IllegalStateException("Invalid permission: $modifiedPermission")
            )

//            viewModelScope.launch {
//                _events.emit(
//                    OnboardingEvent.ShowPermissionDeniedMessage(
//                        permission.split(".").last()
//                    )
//                )
//            }

            return
        }

        _uiState.update { currentState ->
            val updatedPermissions = currentState.permissions.map {
                if (it.permission == permission) {
                    it.copy(isGranted = true)
                } else {
                    it
                }
            }
            currentState.copy(permissions = updatedPermissions.toPersistentList())
        }
    }

    fun onFinishClicked() {
        if (uiState.value.isFinishButtonEnabled) {
            // Perform any first-time setup here if needed
            viewModelScope.launch {
                userDataRepository.setShouldHideOnboarding(true)
                _events.emit(OnboardingEvent.OnboardingSuccess)
            }
        }
    }

}
