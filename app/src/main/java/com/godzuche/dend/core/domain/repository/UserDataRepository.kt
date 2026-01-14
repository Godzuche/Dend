package com.godzuche.dend.core.domain.repository

import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.domain.model.ThemeConfig
import com.godzuche.dend.core.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

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

    /**
     * Sets the preferred dynamic color config.
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)

    /**
     *  Sets the firewall state
     * */
    suspend fun setFirewallState(firewallState: FirewallState)
}