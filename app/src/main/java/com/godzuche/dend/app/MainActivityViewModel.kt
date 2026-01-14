package com.godzuche.dend.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.dend.core.domain.model.ThemeConfig
import com.godzuche.dend.core.domain.model.UserPreferences
import com.godzuche.dend.core.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel(
    userDataRepository: UserDataRepository,
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> =
        userDataRepository.userPreferencesData.map { userData ->
            MainActivityUiState.Success(userData)
        }.stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )

}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(val userData: UserPreferences) : MainActivityUiState {
        override val shouldEnableDynamicTheming = userData.useDynamicColor

        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (userData.themeConfig) {
                ThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                ThemeConfig.LIGHT -> false
                ThemeConfig.DARK -> true
            }

        override val shouldHideOnboarding = userData.shouldHideOnboarding
    }

    /**
     * Returns `true` if the state wasn't loaded yet and it should keep showing the splash screen.
     */
    fun shouldKeepSplashScreen() = this is Loading

    /**
     * Returns `true` if the dynamic color is disabled.
     */
    val shouldEnableDynamicTheming: Boolean get() = false

    /**
     * Returns `true` if dark theme should be used.
     */
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme

    val shouldHideOnboarding: Boolean get() = false
}