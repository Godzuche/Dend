package com.godzuche.dend.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.godzuche.dend.features.main.api.navigation.MainNavKey
import com.godzuche.dend.features.main.impl.navigation.mainScreenEntry
import com.godzuche.dend.features.onboarding.api.navigation.OnboardingGraphNavKey
import com.godzuche.dend.features.onboarding.impl.navigation.onboardingGraphEntry
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    onboardingViewModel: OnboardingViewModel,
    onRequestRolePermission: () -> Unit,
) {
    val backStack = remember {
        mutableStateListOf<Any>(OnboardingGraphNavKey)
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            onboardingGraphEntry(
                onboardingViewModel = onboardingViewModel,
                onOnboardingSuccess = {
                    backStack.remove(OnboardingGraphNavKey)
                    backStack.add(MainNavKey(showOnboardingSuccessMessage = true))
                },
                onRequestRolePermission = onRequestRolePermission,
            )

            mainScreenEntry()
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it }
            ) + fadeIn() togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { -it }
                    ) + fadeOut()
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it }
            ) + fadeIn() togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it }
                    ) + fadeOut()
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it }
            ) + fadeIn() togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it }
                    ) + fadeOut()
        },
    )
}
