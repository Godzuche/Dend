package com.godzuche.dend.features.onboarding.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val _events = MutableSharedFlow<OnboardingEvent>()
    val events = _events.asSharedFlow()

    fun onGrantPermissionClicked() {
        viewModelScope.launch {
            _events.emit(OnboardingEvent.RequestRolePermission)
        }
    }

    fun onPermissionResult(isGranted: Boolean) {
        viewModelScope.launch {
            if (isGranted) {
                // TODO: Perform any first-time setup here if needed.
                _events.emit(OnboardingEvent.NavigateToMainScreen)
            } else {
                // The user denied the permission.
                _events.emit(OnboardingEvent.ShowPermissionDeniedMessage)
            }
        }
    }
}
