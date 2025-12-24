package com.godzuche.dend.features.main.impl.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.features.activity.api.ActivityNavKey
import com.godzuche.dend.features.activity.impl.navigation.activityEntry
import com.godzuche.dend.features.firewall.api.FirewallNavKey
import com.godzuche.dend.features.firewall.impl.navigation.firewallEntry
import com.godzuche.dend.features.firewall.impl.presentation.DashboardViewModel
import com.godzuche.dend.features.main.impl.navigation.MultipleStacksNavigator
import com.godzuche.dend.features.main.impl.navigation.TOP_LEVEL_MAIN_SCREEN_ROUTES
import com.godzuche.dend.features.main.impl.navigation.rememberNavigationState
import com.godzuche.dend.features.main.impl.navigation.toEntries
import com.godzuche.dend.features.main.impl.presentation.components.DenDNavigationBar
import com.godzuche.dend.features.rules.api.RulesNavKey
import com.godzuche.dend.features.rules.impl.navigation.rulesEntry
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
//    showOnboardingSuccessMessage: Boolean = false,
    dashboardViewModel: DashboardViewModel = koinActivityViewModel(),
) {
    val navigationState = rememberNavigationState(
        startRoute = FirewallNavKey,
        topLevelRoutes = TOP_LEVEL_MAIN_SCREEN_ROUTES.keys
    )

    val navigator = remember { MultipleStacksNavigator(navigationState) }

    val entryProvider = entryProvider {
        firewallEntry(
            onNavigateToActivity = {
                navigator.navigate(ActivityNavKey)
            },
            onNavigateToRules = {
                navigator.navigate(RulesNavKey)
            },
        )
        rulesEntry()
        activityEntry()
    }

    val snackbarHostState = remember { SnackbarHostState() }
//    LaunchedEffect(showOnboardingSuccessMessage) {
//        if (showOnboardingSuccessMessage) {
//            snackbarHostState.showSnackbar(
//                message = "Firewall activated!",
//            )
//        }
//    }

    val firewallUiState by dashboardViewModel.firewallUiState.collectAsStateWithLifecycle()

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (firewallUiState.firewallState == FirewallState.ZEN) {
            MaterialTheme.colorScheme.surfaceContainerLow
        } else {
            MaterialTheme.colorScheme.background
        },
        animationSpec = tween(durationMillis = 500),
        label = "background_color_anim"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = animatedBackgroundColor,
        bottomBar = {
            DenDNavigationBar(
                selectedKey = navigationState.topLevelRoute,
                onSelectKey = { navKey ->
                    navigator.navigate(navKey)
                },
            )
        },
        modifier = modifier
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            entries = navigationState.toEntries(
                entryProvider = entryProvider
            ),
            onBack = navigator::goBack,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(durationMillis = 250),
                ) togetherWith fadeOut(
                    animationSpec = tween(durationMillis = 250),
                )
            },
            popTransitionSpec = {
                fadeIn(
                    animationSpec = tween(durationMillis = 250)
                ) togetherWith fadeOut(
                    animationSpec = tween(durationMillis = 250),
                )
            },
            predictivePopTransitionSpec = {
                fadeIn(
                    animationSpec = tween(durationMillis = 250),
                ) togetherWith fadeOut(
                    animationSpec = tween(durationMillis = 250),
                )
            },
        )

    }

}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun MainScreenPreview() = DendTheme {
    MainScreen()
}
