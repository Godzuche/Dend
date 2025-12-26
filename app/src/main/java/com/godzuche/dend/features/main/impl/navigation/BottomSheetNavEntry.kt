package com.godzuche.dend.features.main.impl.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.main.api.navigation.BottomSheetNavKey
import com.godzuche.dend.features.main.impl.presentation.components.AddNumberSheet

@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.bottomSheetEntry(
    onDismiss: () -> Unit,
    onNavigateToCallLog: () -> Unit,
) {
    entry<BottomSheetNavKey>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        AddNumberSheet(
            onDismiss = onDismiss,
            onAddFromRecentsClick = {
                onDismiss()
                onNavigateToCallLog()
            },
        )
    }
}