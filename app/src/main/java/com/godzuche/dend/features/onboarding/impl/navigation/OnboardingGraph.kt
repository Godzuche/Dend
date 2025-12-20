package com.godzuche.dend.features.onboarding.impl.navigation

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.godzuche.dend.app.navigation.rememberNavBackStack
import com.godzuche.dend.features.onboarding.api.navigation.CorePermissionsScreenNavKey
import com.godzuche.dend.features.onboarding.api.navigation.WelcomeScreenNavKey
import com.godzuche.dend.features.onboarding.impl.presentation.components.RoleSettingsDialog
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingEvent
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun OnboardingGraph(
    modifier: Modifier = Modifier,
    onboardingViewModel: OnboardingViewModel = koinActivityViewModel<OnboardingViewModel>(),
    onOnboardingSuccess: () -> Unit,
) {
    val onboardingBackStack = rememberNavBackStack<NavKey>(WelcomeScreenNavKey)

    var showSettingsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        onboardingViewModel.events.collect { event ->
            when (event) {
                is OnboardingEvent.ShowManualRoleSettingsGuidance -> {
                    showSettingsDialog = true
                }

                is OnboardingEvent.NavigateToCorePermissions -> {
                    onboardingBackStack.add(CorePermissionsScreenNavKey)
                }

                is OnboardingEvent.OnboardingSuccess -> {
////                    Toast.makeText(context, "Firewall activated!", Toast.LENGTH_SHORT).show()
//                    backStack.remove(OnboardingGraphNavKey)
//                    backStack.add(MainNavKey(showOnboardingSuccessMessage = true))
                    onOnboardingSuccess()
                }

                is OnboardingEvent.ShowPermissionDeniedMessage -> {
                    Toast.makeText(
                        context,
                        "\"${event.permission}\" Permission is required for app functionalities",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        }
    }

    if (showSettingsDialog) {
        RoleSettingsDialog(
            onDismiss = { showSettingsDialog = false },
            onGoToSettings = {
                showSettingsDialog = false
                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                context.startActivity(intent)
            }
        )
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
            onboardingRolePermissionEntry()
            onboardingCorePermissionsEntry()
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