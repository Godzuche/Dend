package com.godzuche.dend.features.rules.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.rules.api.CallLogNavKey
import com.godzuche.dend.features.rules.impl.presentation.CustomCallLogScreen

fun EntryProviderScope<NavKey>.callLogEntry(
    onBackPress: () -> Unit,
) {
    entry<CallLogNavKey> {
        CustomCallLogScreen(onNavigateBack = onBackPress)
    }
}