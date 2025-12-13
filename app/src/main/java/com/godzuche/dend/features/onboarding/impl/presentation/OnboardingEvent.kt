package com.godzuche.dend.features.onboarding.impl.presentation

sealed interface OnboardingEvent {
    data object RequestRolePermission : OnboardingEvent
    data object NavigateToCorePermissions : OnboardingEvent
    data object RequestCorePermissions : OnboardingEvent
    data object OnboardingSuccess : OnboardingEvent
    data object ShowPermissionDeniedMessage : OnboardingEvent
}