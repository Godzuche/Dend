package com.godzuche.dend.features.main.impl.presentation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.godzuche.dend.features.main.impl.navigation.DashboardNavKey
import com.godzuche.dend.features.main.impl.navigation.LogsNavKey
import com.godzuche.dend.features.main.impl.navigation.Navigator
import com.godzuche.dend.features.main.impl.navigation.RulesNavKey
import com.godzuche.dend.features.main.impl.navigation.TOP_LEVEL_MAIN_SCREEN_ROUTES
import com.godzuche.dend.features.main.impl.navigation.rememberNavigationState
import com.godzuche.dend.features.main.impl.navigation.toEntries
import com.godzuche.dend.features.main.impl.presentation.components.DenDNavigationBar

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
//    val backStack = remember {
//        mutableStateListOf<Any>(DashboardNavKey)
//    }

    val navigationState = rememberNavigationState(
        startRoute = DashboardNavKey,
        topLevelRoutes = TOP_LEVEL_MAIN_SCREEN_ROUTES.keys
    )

    val navigator = remember { Navigator(navigationState) }

    val entryProvider = /*entryProvider {*/
//        featureASection(onSubRouteClick = { navigator.navigate(RouteA1) })
//        featureBSection(onSubRouteClick = { navigator.navigate(RouteB1) })
//        featureCSection(onSubRouteClick = { navigator.navigate(RouteC1) })
//    }
        entryProvider {
            entry<DashboardNavKey> {
                DummyScreen("Dashboard")
            }
            entry<RulesNavKey> {
                DummyScreen("Rules")
            }
            entry<LogsNavKey> {
                DummyScreen("Logs")
            }
        }

    Scaffold(
        bottomBar = {
            DenDNavigationBar(
                selectedKey = navigationState.topLevelRoute,
                onSelectKey = { navKey ->
                    println("BottomNavigate to key: $navKey")
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
//            transitionSpec = {
//                slideInHorizontally(
//                    initialOffsetX = { it }
//                ) + fadeIn() togetherWith
//                        slideOutHorizontally(
//                            targetOffsetX = { -it }
//                        ) + fadeOut()
//            },
//            popTransitionSpec = {
//                slideInHorizontally(
//                    initialOffsetX = { -it }
//                ) + fadeIn() togetherWith
//                        slideOutHorizontally(
//                            targetOffsetX = { it }
//                        ) + fadeOut()
//            },
//            predictivePopTransitionSpec = {
//                slideInHorizontally(
//                    initialOffsetX = { -it }
//                ) + fadeIn() togetherWith
//                        slideOutHorizontally(
//                            targetOffsetX = { it }
//                        ) + fadeOut()
//            },
        )

    }

}

@Composable
fun DummyScreen(
    title: String,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = title,
            fontSize = 45.sp,
        )
    }
}