package com.godzuche.dend.features.main.impl.navigation

import androidx.annotation.DrawableRes
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.R
import kotlinx.serialization.Serializable

data class NavBarItem(
    @DrawableRes
    val iconRes: Int,
    val title: String,
)

internal val TOP_LEVEL_MAIN_SCREEN_ROUTES = mapOf(
    DashboardNavKey to NavBarItem(iconRes = R.drawable.security_24dp, title = "Dashboard"),
    RulesNavKey to NavBarItem(iconRes = R.drawable.cloud_off_24dp, title = "Rules"),
    LogsNavKey to NavBarItem(iconRes = R.drawable.admin_panel_settings_24dp, title = "Log"),
)

//Todo: Move to feats
@Serializable
data object DashboardNavKey: NavKey
@Serializable
data object RulesNavKey: NavKey
@Serializable
data object LogsNavKey: NavKey