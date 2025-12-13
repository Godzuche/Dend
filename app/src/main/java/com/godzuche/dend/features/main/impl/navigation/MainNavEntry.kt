package com.godzuche.dend.features.main.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.godzuche.dend.features.main.api.navigation.MainNavKey
import com.godzuche.dend.features.main.impl.presentation.MainScreen

fun EntryProviderScope<Any>.mainScreenEntry() {
    entry<MainNavKey> { key ->
        MainScreen(
            showOnboardingSuccessMessage = key.showOnboardingSuccessMessage,
        )
    }
}