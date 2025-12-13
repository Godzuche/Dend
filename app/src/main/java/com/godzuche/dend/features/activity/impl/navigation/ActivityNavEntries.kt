package com.godzuche.dend.features.activity.impl.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.activity.api.ActivityNavKey

fun EntryProviderScope<NavKey>.activityEntry() {
    entry<ActivityNavKey> {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "Activities",
                fontSize = 45.sp,
            )
        }
    }
}