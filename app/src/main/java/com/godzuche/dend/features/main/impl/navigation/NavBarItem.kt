package com.godzuche.dend.features.main.impl.navigation

import androidx.annotation.DrawableRes
import com.godzuche.dend.R
import com.godzuche.dend.features.activity.api.ActivityNavKey
import com.godzuche.dend.features.firewall.api.FirewallNavKey
import com.godzuche.dend.features.rules.api.RulesNavKey

data class NavBarItem(
    @param: DrawableRes
    val iconRes: Int,
    val title: String,
)

internal val TOP_LEVEL_MAIN_SCREEN_ROUTES = mapOf(
    FirewallNavKey to NavBarItem(iconRes = R.drawable.shield_24dp, title = "Dashboard"),
    RulesNavKey to NavBarItem(iconRes = R.drawable.checklist_24dp, title = "Rules"),
    ActivityNavKey to NavBarItem(iconRes = R.drawable.history_24dp, title = "Activity"),
)
