package com.godzuche.dend.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

enum class ThemeConfig {
    FOLLOW_SYSTEM,
    LIGHT,
    DARK,
}

/**
 * Class summarizing user settings/preferences data
 */
@Serializable
data class UserPreferencesData(
    val themeConfig: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    val useDynamicColor: Boolean = false,
    val shouldHideOnboarding: Boolean = false,
)

data class UserPreferences(
    val themeConfig: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    val useDynamicColor: Boolean = false,
    val shouldHideOnboarding: Boolean = false,
)

interface UserDataRepository {
    /**
     * Stream of [UserPreferences]
     */
    val userPreferencesData: Flow<UserPreferences>

    suspend fun setThemeConfig(themeConfig: ThemeConfig)

    /**
     * Sets whether the user has completed the onboarding process.
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)
}