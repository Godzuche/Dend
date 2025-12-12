package com.godzuche.dend.features.onboarding.impl.navigation

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
import com.godzuche.dend.features.onboarding.api.navigation.WelcomeScreenNavKey
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel

@Composable
fun OnboardingGraph(
    onboardingViewModel: OnboardingViewModel,
    modifier: Modifier = Modifier,
) {
    val onboardingBackStack = remember {
        mutableStateListOf<Any>(WelcomeScreenNavKey)
    }

    NavDisplay(
        backStack = onboardingBackStack,
        onBack = { onboardingBackStack.removeLastOrNull() },
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            onboardingWelcomeEntry(backStack = onboardingBackStack)
            onboardingPermissionEntry(
                onGrantPermissionClick = onboardingViewModel::onGrantPermissionClicked,
            )
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