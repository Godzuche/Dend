package com.godzuche.dend.app.di

import com.godzuche.dend.app.MainActivityViewModel
import com.godzuche.dend.core.presentation.messaging.UiEventBus
import com.godzuche.dend.features.firewall.impl.presentation.DashboardViewModel
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel
import com.godzuche.dend.features.rules.impl.presentation.RulesViewModel
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
    viewModelOf(::DashboardViewModel)
    viewModelOf(::RulesViewModel)
    single { UiEventBus() }
}