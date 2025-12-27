package com.godzuche.dend.core.data.di

import com.godzuche.dend.core.data.PhoneCallDataSource
import com.godzuche.dend.core.data.datastore.DenDPreferencesDataSource
import com.godzuche.dend.core.data.repository.OfflineFirstUserDataRepository
import com.godzuche.dend.core.domain.repository.UserDataRepository
import com.godzuche.dend.features.rules.impl.data.repository.RulesRepositoryImpl
import com.godzuche.dend.features.rules.impl.domain.repository.RulesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::DenDPreferencesDataSource)
//    singleOf(::OfflineFirstUserDataRepository) { bind<UserDataRepository>() }
    single<UserDataRepository> {
        val ioDispatcher = get<CoroutineDispatcher>(named(DendDispatchers.IO))

        OfflineFirstUserDataRepository(
            preferencesDataSource = get(),
            ioDispatcher = ioDispatcher,
        )
    }

    single<PhoneCallDataSource> {
        val ioDispatcher = get<CoroutineDispatcher>(named(DendDispatchers.IO))
        val scope = get<CoroutineScope>(named<ApplicationScope>())

        PhoneCallDataSource(
            context = androidContext(),
            ioDispatcher = ioDispatcher,
            scope = scope,
        )
    }

    singleOf(::RulesRepositoryImpl).bind<RulesRepository>()
}