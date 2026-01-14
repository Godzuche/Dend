package com.godzuche.dend.core.data.model

import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.domain.model.ThemeConfig
import kotlinx.serialization.Serializable


/**
 * Class summarizing user settings/preferences data
 */
@Serializable
data class UserPreferencesData(
    val themeConfig: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    val useDynamicColor: Boolean = false,
    val shouldHideOnboarding: Boolean = false,
    val firewallState: FirewallState = FirewallState.OFF,
)