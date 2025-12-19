package com.godzuche.dend.core.data.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

object ApplicationScope

val coroutineScopesModule = module {
    single<CoroutineScope>(named<ApplicationScope>()) {
        val defaultDispatcher = get<CoroutineDispatcher>(named(DendDispatchers.Default))
        CoroutineScope(SupervisorJob() + defaultDispatcher)
    }
}