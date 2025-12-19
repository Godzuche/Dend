package com.godzuche.dend.app

import android.app.role.RoleManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.trace
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.godzuche.dend.app.util.isSystemInDarkTheme
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.viewmodel.koinActivityViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var themeSettings by mutableStateOf(
            ThemeSettings(
                darkTheme = resources.configuration.isSystemInDarkTheme,
                isDynamicThemingEnabled = MainActivityUiState.Loading.shouldEnableDynamicTheming,
            ),
        )

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    viewModel.uiState,
                ) { systemDark, uiState ->
                    ThemeSettings(
                        darkTheme = uiState.shouldUseDarkTheme(systemDark),
                        isDynamicThemingEnabled = uiState.shouldEnableDynamicTheming,
                    )
                }
                    .onEach {
                        themeSettings = it
                    }
                    .map { it.darkTheme }
                    .distinctUntilChanged()
                    .collect { darkTheme ->
                        trace("dendEdgeToEdge") {
                            // Turn off the decor fitting system windows, which allows us to handle insets,
                            // including IME animations, and go edge-to-edge.
                            // This is the same parameters as the default enableEdgeToEdge call, but we manually
                            // resolve whether or not to show dark theme using uiState, since it can be different
                            // than the configuration's dark theme value based on the user preference.
                            enableEdgeToEdge(
                                statusBarStyle = SystemBarStyle.auto(
                                    lightScrim = Color.TRANSPARENT,
                                    darkScrim = Color.TRANSPARENT,
                                ) { darkTheme },
                                navigationBarStyle = SystemBarStyle.auto(
                                    lightScrim = lightScrim,
                                    darkScrim = darkScrim,
                                ) { darkTheme },
                            )
                        }
                    }
            }
        }

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        setContent {
            val onboardingViewModel = koinActivityViewModel<OnboardingViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val callScreeningRoleLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    Log.d("MainActivity", "Role granted. data: ${result.data?.data}")
                    onboardingViewModel.onRolePermissionResult(true)
                } else {
                    Log.d("MainActivity", "Role denied")
                    onboardingViewModel.onRolePermissionResult(false)
                }
            }

            val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
            fun launchRoleRequest() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                callScreeningRoleLauncher.launch(intent)
//            }
            }

            DendTheme(
                darkTheme = themeSettings.darkTheme,
                isDynamicThemingEnabled = themeSettings.isDynamicThemingEnabled,
            ) {
                App(
                    shouldHideOnboarding = uiState.shouldHideOnboarding,
                    onRequestRolePermission = {
                        if (roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                            // The role is already granted. Maybe the user went to settings and did it manually.
                            // Proceed to the next step.
                            onboardingViewModel.onRolePermissionResult(true)
                        } else {
                            launchRoleRequest()
                        }
                    },
                )
            }
        }
    }

}


/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

/**
 * Class for the system theme settings.
 * This wrapping class allows us to combine all the changes and prevent unnecessary recompositions.
 */
data class ThemeSettings(
    val darkTheme: Boolean,
    val isDynamicThemingEnabled: Boolean,
)
