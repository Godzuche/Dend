package com.godzuche.dend.features.onboarding.impl.navigation

import android.widget.Toast
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.godzuche.dend.features.onboarding.api.navigation.CorePermissionsScreenNavKey
import com.godzuche.dend.features.onboarding.api.navigation.WelcomeScreenNavKey
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingEvent
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel

@Composable
fun OnboardingGraph(
    onboardingViewModel: OnboardingViewModel,
    onOnboardingSuccess: () -> Unit,
    onRequestRolePermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val onboardingBackStack = remember {
        mutableStateListOf<Any>(WelcomeScreenNavKey)
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        onboardingViewModel.events.collect { event ->
            when (event) {
                is OnboardingEvent.RequestRolePermission -> {
                    onRequestRolePermission()
                }

                is OnboardingEvent.NavigateToCorePermissions -> {
                    onboardingBackStack.add(CorePermissionsScreenNavKey)
                }

                is OnboardingEvent.RequestCorePermissions -> {
                    // Launch the new multi-permission request from the Activity.
//                    onRequestCorePermissions(
//                        arrayOf(
//                            Manifest.permission.ANSWER_PHONE_CALLS,
//                            Manifest.permission.READ_CONTACTS,
//                            Manifest.permission.READ_CALL_LOG,
//                            Manifest.permission.WRITE_CALL_LOG
//                        )
//                    )
                }

                is OnboardingEvent.OnboardingSuccess -> {
////                    Toast.makeText(context, "Firewall activated!", Toast.LENGTH_SHORT).show()
////                    backStack.clear()
//                    backStack.remove(OnboardingGraphNavKey)
//                    backStack.add(MainNavKey(showOnboardingSuccessMessage = true))
                    onOnboardingSuccess()
                }

                is OnboardingEvent.ShowPermissionDeniedMessage -> {
                    Toast.makeText(
                        context,
                        "Permission is required to block calls.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
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
            onboardingRolePermissionEntry(
                onGrantPermissionClick = onboardingViewModel::onGrantRolePermissionClicked,
            )
            onboardingCorePermissionsEntry(
                onboardingViewModel = onboardingViewModel,
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