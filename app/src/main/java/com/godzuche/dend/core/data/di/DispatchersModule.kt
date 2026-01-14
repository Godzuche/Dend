package com.godzuche.dend.core.data.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatchersModule = module {

    single<CoroutineDispatcher>(named(DendDispatchers.IO)) {
        Dispatchers.IO
    }

    single<CoroutineDispatcher>(named(DendDispatchers.Default)) {
        Dispatchers.Default
    }
}

enum class DendDispatchers {
    Default,
    IO,
}