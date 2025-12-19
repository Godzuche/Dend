package com.godzuche.dend.app

import android.app.Application
import com.godzuche.dend.app.di.appModule
import com.godzuche.dend.core.data.datastore.di.dataStoreModule
import com.godzuche.dend.core.data.di.coroutineScopesModule
import com.godzuche.dend.core.data.di.dataModule
import com.godzuche.dend.core.data.di.dispatchersModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DendApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DendApplication)
            modules(
                appModule,
                dataModule,
                dispatchersModule,
                dataStoreModule,
                coroutineScopesModule,
            )
        }
    }
}
