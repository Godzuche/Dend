package com.godzuche.dend.features.firewall.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.firewall.api.FirewallNavKey
import com.godzuche.dend.features.firewall.impl.presentation.DashboardScreen

fun EntryProviderScope<NavKey>.firewallEntry(
    onNavigateToActivity: () -> Unit,
    onNavigateToRules: () -> Unit,
) {
    entry<FirewallNavKey> {
        DashboardScreen(
            onNavigateToActivity = onNavigateToActivity,
            onNavigateToRules = onNavigateToRules,
        )
    }
}