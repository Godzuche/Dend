package com.godzuche.dend.features.onboarding.impl.presentation

sealed interface OnboardingEvent {
    data object RequestRolePermission : OnboardingEvent
    data object NavigateToMainScreen : OnboardingEvent
    data object ShowPermissionDeniedMessage : OnboardingEvent
}