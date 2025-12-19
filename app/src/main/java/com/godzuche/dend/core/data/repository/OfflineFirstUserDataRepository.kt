package com.godzuche.dend.core.data.repository

import com.godzuche.dend.core.data.ThemeConfig
import com.godzuche.dend.core.data.UserDataRepository
import com.godzuche.dend.core.data.UserPreferences
import com.godzuche.dend.core.data.datastore.DenDPreferencesDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class OfflineFirstUserDataRepository(
    private val preferencesDataSource: DenDPreferencesDataSource,
//    private val analyticsHelper: AnalyticsHelper,
//    private val ioDispatcher: CoroutineDispatcher,
) : UserDataRepository {

    override val userPreferencesData: Flow<UserPreferences> =
        preferencesDataSource.userPreferencesData

    override suspend fun setThemeConfig(themeConfig: ThemeConfig) {
//        withContext(ioDispatcher) {
        preferencesDataSource.setDarkThemeConfig(themeConfig)
//        }
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
//        withContext(ioDispatcher) {
        preferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
//        }
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
//        withContext(ioDispatcher) {
        preferencesDataSource.setDynamicColorPreference(useDynamicColor)
//        }
    }
}