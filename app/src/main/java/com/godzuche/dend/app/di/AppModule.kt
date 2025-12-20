package com.godzuche.dend.app.di

import com.godzuche.dend.app.MainActivityViewModel
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

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