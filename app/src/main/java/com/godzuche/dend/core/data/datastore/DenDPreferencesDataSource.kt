package com.godzuche.dend.core.data.datastore

import androidx.datastore.core.DataStore
import com.godzuche.dend.core.data.ThemeConfig
import com.godzuche.dend.core.data.UserPreferences
import com.godzuche.dend.core.data.UserPreferencesData
import kotlinx.coroutines.flow.map

class DenDPreferencesDataSource(
    private val userPreferences: DataStore<UserPreferencesData>,
) {
    val userPreferencesData = userPreferences.data
        .map {
            UserPreferences(
                themeConfig = it.themeConfig,
                useDynamicColor = it.useDynamicColor,
                shouldHideOnboarding = it.shouldHideOnboarding,
            )
        }

    suspend fun setDarkThemeConfig(themeConfig: ThemeConfig) {
        userPreferences.updateData {
            it.copy(
                themeConfig = themeConfig,
            )
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy(
                shouldHideOnboarding = shouldHideOnboarding,
            )
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy(
                useDynamicColor = useDynamicColor,
            )
        }
    }

}