package com.godzuche.dend.features.onboarding.impl.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.godzuche.dend.features.onboarding.api.navigation.OnboardingGraphNavKey
import com.godzuche.dend.features.onboarding.api.navigation.PermissionScreenNavKey
import com.godzuche.dend.features.onboarding.api.navigation.WelcomeScreenNavKey
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel
import com.godzuche.dend.features.onboarding.impl.presentation.PermissionScreen
import com.godzuche.dend.features.onboarding.impl.presentation.WelcomeScreen

fun EntryProviderScope<Any>.onboardingGraphEntry(
    onboardingViewModel: OnboardingViewModel,
) {
    entry<OnboardingGraphNavKey> {
        OnboardingGraph(
            onboardingViewModel = onboardingViewModel,
        )
    }
}

fun EntryProviderScope<Any>.onboardingWelcomeEntry(
    backStack: SnapshotStateList<Any>,
) {
    entry<WelcomeScreenNavKey> {
        WelcomeScreen(
            onGetStartedClick = {
                backStack.add(PermissionScreenNavKey)
            },
        )
    }
}

fun EntryProviderScope<Any>.onboardingPermissionEntry(
    onGrantPermissionClick: () -> Unit,
) {
    entry<PermissionScreenNavKey> {
        PermissionScreen(
            onGrantPermissionClick = onGrantPermissionClick,
        )
    }
}
