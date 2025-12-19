package com.godzuche.dend.features.onboarding.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.onboarding.api.navigation.CorePermissionsScreenNavKey
import com.godzuche.dend.features.onboarding.api.navigation.OnboardingGraphNavKey
import com.godzuche.dend.features.onboarding.api.navigation.RolePermissionScreenNavKey
import com.godzuche.dend.features.onboarding.api.navigation.WelcomeScreenNavKey
import com.godzuche.dend.features.onboarding.impl.presentation.CorePermissionsScreen
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel
import com.godzuche.dend.features.onboarding.impl.presentation.RolePermissionScreen
import com.godzuche.dend.features.onboarding.impl.presentation.WelcomeScreen

fun EntryProviderScope<NavKey>.onboardingGraphEntry(
    onboardingViewModel: OnboardingViewModel,
    onOnboardingSuccess: () -> Unit,
    onRequestRolePermission: () -> Unit,
) {
    entry<OnboardingGraphNavKey> {
        OnboardingGraph(
            onboardingViewModel = onboardingViewModel,
            onOnboardingSuccess = onOnboardingSuccess,
            onRequestRolePermission = onRequestRolePermission,
        )
    }
}

fun EntryProviderScope<NavKey>.onboardingWelcomeEntry(
    backStack: NavBackStack<NavKey>,
) {
    entry<WelcomeScreenNavKey> {
        WelcomeScreen(
            onGetStartedClick = {
                backStack.add(RolePermissionScreenNavKey)
            },
        )
    }
}

fun EntryProviderScope<NavKey>.onboardingRolePermissionEntry(
    onGrantPermissionClick: () -> Unit,
) {
    entry<RolePermissionScreenNavKey> {
        RolePermissionScreen(
            onGrantPermissionClick = onGrantPermissionClick,
        )
    }
}

fun EntryProviderScope<NavKey>.onboardingCorePermissionsEntry(
    onboardingViewModel: OnboardingViewModel,
) {
    entry<CorePermissionsScreenNavKey> {
        CorePermissionsScreen(
            onboardingViewModel = onboardingViewModel,
        )
    }
}
