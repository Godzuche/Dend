package com.godzuche.dend.core.data.repository

import com.godzuche.dend.core.data.datastore.DenDPreferencesDataSource
import com.godzuche.dend.core.domain.model.ThemeConfig
import com.godzuche.dend.core.domain.model.UserPreferences
import com.godzuche.dend.core.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow

class OfflineFirstUserDataRepository(
    private val preferencesDataSource: DenDPreferencesDataSource,
//    private val ioDispatcher: CoroutineDispatcher,
) : UserDataRepository {

    override val userPreferencesData: Flow<UserPreferences> =
        preferencesDataSource.userPreferencesData

    override suspend fun setThemeConfig(themeConfig: ThemeConfig) {
        preferencesDataSource.setDarkThemeConfig(themeConfig)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        preferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        preferencesDataSource.setDynamicColorPreference(useDynamicColor)
    }
}