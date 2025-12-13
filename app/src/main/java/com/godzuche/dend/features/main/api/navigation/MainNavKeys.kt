package com.godzuche.dend.features.main.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class MainNavKey(
    val showOnboardingSuccessMessage: Boolean,
): NavKey