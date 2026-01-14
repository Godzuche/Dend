package com.godzuche.dend.features.activity.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.activity.api.ActivityNavKey
import com.godzuche.dend.features.activity.impl.presentation.ActivityScreen

fun EntryProviderScope<NavKey>.activityEntry() {
    entry<ActivityNavKey> {
        ActivityScreen()
    }
}