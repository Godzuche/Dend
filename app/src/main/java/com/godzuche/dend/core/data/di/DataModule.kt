package com.godzuche.dend.core.data.di

import com.godzuche.dend.core.data.datastore.DenDPreferencesDataSource
import com.godzuche.dend.core.data.repository.OfflineFirstUserDataRepository
import com.godzuche.dend.core.domain.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
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
}