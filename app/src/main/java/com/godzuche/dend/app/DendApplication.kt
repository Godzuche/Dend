package com.godzuche.dend.app

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.godzuche.dend.core.data.UserDataRepository
import com.godzuche.dend.core.data.UserPreferencesData
import com.godzuche.dend.core.data.datastore.DenDPreferencesDataSource
import com.godzuche.dend.core.data.datastore.DenDPreferencesSerializer
import com.godzuche.dend.core.data.repository.OfflineFirstUserDataRepository
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

class DendApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DendApplication)
            modules(appModule,
                dataModule,
                dispatchersModule,
                dataStoreModule,
                coroutineScopesModule
                )
        }
    }
}

// Todo: move to di
val appModule = module {
//    includes(
//        dataModule,
//        dispatchersModule,
//        dataStoreModule,
//        coroutineScopesModule
//    )

    viewModelOf(::MainActivityViewModel)
    viewModelOf(::OnboardingViewModel)
}

val dataModule = module {
    singleOf (::DenDPreferencesDataSource)
    singleOf(::OfflineFirstUserDataRepository) { bind<UserDataRepository>() }
}

private const val DATA_STORE_FILE_NAME = "user_preferences.json"

val dataStoreModule = module {
    single { DenDPreferencesSerializer }

    single<DataStore<UserPreferencesData>> {
        val ioDispatcher = get<CoroutineDispatcher>(named(DendDispatchers.IO))
        val scope = get<CoroutineScope>(named<ApplicationScope>())
        val userPreferencesSerializer = get<DenDPreferencesSerializer>()

        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = {
                    // return default value
                    UserPreferencesData()
                }
            ),
            migrations = listOf(),
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
            produceFile = {
                androidContext().dataStoreFile(DATA_STORE_FILE_NAME)
            }
        )
    }
}

val dispatchersModule = module {

    // Replaces @Provides @Dispatcher(IO)
    // We provide a CoroutineDispatcher and name it using the enum value.
    single<CoroutineDispatcher>(named(DendDispatchers.IO)) {
        Dispatchers.IO
    }

    // Replaces @Provides @Dispatcher(Default)
    single<CoroutineDispatcher>(named(DendDispatchers.Default)) {
        Dispatchers.Default
    }
}

enum class DendDispatchers {
    Default,
    IO,
}

object ApplicationScope

val coroutineScopesModule = module {
    single<CoroutineScope>(named<ApplicationScope>()) {
        val defaultDispatcher = get<CoroutineDispatcher>(named(DendDispatchers.Default))
        CoroutineScope(SupervisorJob() + defaultDispatcher)
    }
}