package com.godzuche.dend.core.data.di

import com.godzuche.dend.features.activity.impl.data.database.ActivityDatabase
import com.godzuche.dend.features.rules.impl.data.database.RulesDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        RulesDatabase.getDatabase(androidContext())
    }

    single {
        ActivityDatabase.getDatabase(androidContext())
    }

    single { get<RulesDatabase>().ruleDao() }
    single { get<ActivityDatabase>().blockedCallDao() }
}