package com.godzuche.dend.features.main.impl.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.godzuche.dend.R
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.presentation.ShowSnackbarAction
import com.godzuche.dend.core.presentation.SnackbarAction
import com.godzuche.dend.core.presentation.UiEvent
import com.godzuche.dend.core.presentation.UiText
import com.godzuche.dend.core.presentation.messaging.UiEventBus
import com.godzuche.dend.core.presentation.utils.ObserveAsEvent
import com.godzuche.dend.core.presentation.utils.toUiText
import com.godzuche.dend.features.activity.api.ActivityNavKey
import com.godzuche.dend.features.activity.impl.navigation.activityEntry
import com.godzuche.dend.features.firewall.api.FirewallNavKey
import com.godzuche.dend.features.firewall.impl.navigation.firewallEntry
import com.godzuche.dend.features.firewall.impl.presentation.DashboardViewModel
import com.godzuche.dend.features.main.api.navigation.BottomSheetNavKey
import com.godzuche.dend.features.main.impl.navigation.BottomSheetSceneStrategy
import com.godzuche.dend.features.main.impl.navigation.MultipleStacksNavigator
import com.godzuche.dend.features.main.impl.navigation.TOP_LEVEL_MAIN_SCREEN_ROUTES
import com.godzuche.dend.features.main.impl.navigation.bottomSheetEntry
import com.godzuche.dend.features.main.impl.navigation.rememberNavigationState
import com.godzuche.dend.features.main.impl.navigation.toEntries
import com.godzuche.dend.features.main.impl.presentation.components.DenDNavigationBar
import com.godzuche.dend.features.rules.api.CallLogNavKey
import com.godzuche.dend.features.rules.api.RulesNavKey
import com.godzuche.dend.features.rules.impl.navigation.callLogEntry
import com.godzuche.dend.features.rules.impl.navigation.rulesEntry
import com.godzuche.dend.features.rules.impl.presentation.RulesUiEvent
import com.godzuche.dend.features.rules.impl.presentation.RulesViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    dashboardViewModel: DashboardViewModel = koinActivityViewModel(),
    rulesViewModel: RulesViewModel = koinActivityViewModel(),
    uiEventBus: UiEventBus = koinInject(),
) {
    val navigationState = rememberNavigationState(
        startRoute = FirewallNavKey,
        topLevelRoutes = TOP_LEVEL_MAIN_SCREEN_ROUTES.keys
    )

    val navigator = remember { MultipleStacksNavigator(navigationState) }

    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

    val entryProvider = entryProvider {
        firewallEntry(
            onNavigateToActivity = {
                navigator.navigate(ActivityNavKey)
            },
//            onNavigateToRules = {
//                navigator.navigate(RulesNavKey)
//            },
        )

        rulesEntry()

        activityEntry()

        bottomSheetEntry(
            onDismiss = navigator::goBack,
            onNavigateToCallLog = {
                navigator.navigate(CallLogNavKey)
            },
        )

        callLogEntry(
            onBackPress = navigator::goBack,
        )
    }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

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

    ObserveAsEvent(
        flow = rulesViewModel.events,
    ) { event ->
        val uiEvent = when (event) {
            is RulesUiEvent.RuleAdded -> {
                val text =
                    UiText.StringResource(
                        R.string.rule_added_successfully,
                        listOf(
                            event.number,
                            event.selectedRulesTab.title,
                        )
                    )
                UiEvent.ShowSnackbar(text)
            }

            is RulesUiEvent.RuleRemoved -> {
                val text =
                    UiText.StringResource(
                        R.string.rule_removed_successfully,
                        listOf(
                            event.contactLabel,
                            event.selectedRulesTab.title,
                        ),
                    )

                UiEvent.ShowSnackbar(
                    message = text,
                    snackbarAction = ShowSnackbarAction.UndoRemoveRule(
                        undoAction = SnackbarAction.UNDO,
                    ),
                )
            }

            is RulesUiEvent.OperationFailed -> {
                val text = event.error.toUiText()
                UiEvent.ShowSnackbar(text)
            }
        }

        uiEventBus.sendEvent(uiEvent)

    }

    ObserveAsEvent(
        flow = uiEventBus.events,
        uiEventBus,
        snackbarHostState,
        context,
    ) { event ->
        when (event) {
            is UiEvent.ShowSnackbar -> {
                val message = event.message.asString(context)
                val actionLabel = event.snackbarAction?.action?.label

                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    duration = if (actionLabel == null) {
                        SnackbarDuration.Short
                    } else SnackbarDuration.Short,
                )

                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        // User tapped "Undo". Send the corresponding event back.
                        when (event.snackbarAction) {
                            is ShowSnackbarAction.UndoRemoveRule -> {
                                if (event.snackbarAction.action == SnackbarAction.UNDO) {
                                    // A better way would be to have a specific event for this.
                                    // For now, let's call the ViewModel directly.
                                    rulesViewModel.onUndoRemove()
                                }
                            }

                            else -> Unit
                        }
                    }

                    SnackbarResult.Dismissed -> {
                        // Snackbar timed out or was dismissed
                        when (event.snackbarAction) {
                            is ShowSnackbarAction.UndoRemoveRule -> {
                                if (event.snackbarAction.action == SnackbarAction.UNDO) {
                                    rulesViewModel.onCommitRemove()
                                }
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = animatedBackgroundColor,
        floatingActionButton = {
            AnimatedVisibility(
                visible = navigationState.currentRoute is RulesNavKey,
                enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut(),
            ) {
                FloatingActionButton(
                    onClick = {
                        navigator.navigate(BottomSheetNavKey)
                    },
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.add_24dp),
                        contentDescription = "Add Rule",
                    )
                }
            }
        },
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
            sceneStrategy = bottomSheetStrategy,
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
