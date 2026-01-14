package com.godzuche.dend.core.data.repository

import com.godzuche.dend.core.data.datastore.DenDPreferencesDataSource
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.domain.model.ThemeConfig
import com.godzuche.dend.core.domain.model.UserPreferences
import com.godzuche.dend.core.domain.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class OfflineFirstUserDataRepository(
    private val preferencesDataSource: DenDPreferencesDataSource,
    ioDispatcher: CoroutineDispatcher,
) : UserDataRepository {

    override val userPreferencesData: Flow<UserPreferences> =
        preferencesDataSource.userPreferencesData
            .flowOn(ioDispatcher)

    override suspend fun setThemeConfig(themeConfig: ThemeConfig) {
        preferencesDataSource.setDarkThemeConfig(themeConfig)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        preferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        preferencesDataSource.setDynamicColorPreference(useDynamicColor)
    }

    override suspend fun setFirewallState(firewallState: FirewallState) {
        preferencesDataSource.setFirewallState(firewallState)
    }
}