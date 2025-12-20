package com.godzuche.dend.features.onboarding.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.onboarding.api.navigation.CorePermissionsScreenNavKey
import com.godzuche.dend.features.onboarding.api.navigation.OnboardingGraphNavKey
import com.godzuche.dend.features.onboarding.api.navigation.RolePermissionScreenNavKey
import com.godzuche.dend.features.onboarding.api.navigation.WelcomeScreenNavKey
import com.godzuche.dend.features.onboarding.impl.presentation.CorePermissionsScreen
import com.godzuche.dend.features.onboarding.impl.presentation.RolePermissionScreen
import com.godzuche.dend.features.onboarding.impl.presentation.WelcomeScreen

fun EntryProviderScope<NavKey>.onboardingGraphEntry(
    onOnboardingSuccess: () -> Unit,
) {
    entry<OnboardingGraphNavKey> {
        OnboardingGraph(
            onOnboardingSuccess = onOnboardingSuccess,
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

fun EntryProviderScope<NavKey>.onboardingRolePermissionEntry() {
    entry<RolePermissionScreenNavKey> {
        RolePermissionScreen()
    }
}

fun EntryProviderScope<NavKey>.onboardingCorePermissionsEntry() {
    entry<CorePermissionsScreenNavKey> {
        CorePermissionsScreen()
    }
}
