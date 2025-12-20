package com.godzuche.dend.core.domain.model

data class UserPreferences(
    val themeConfig: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    val useDynamicColor: Boolean = false,
    val shouldHideOnboarding: Boolean = false,
)