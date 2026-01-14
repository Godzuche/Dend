package com.godzuche.dend.features.rules.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.rules.api.RulesNavKey
import com.godzuche.dend.features.rules.impl.presentation.RulesScreen

fun EntryProviderScope<NavKey>.rulesEntry() {
    entry<RulesNavKey> {
        RulesScreen()
    }
}