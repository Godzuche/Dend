package com.godzuche.dend.features.rules.impl.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.godzuche.dend.core.data.database.InstantConverter
import com.godzuche.dend.features.rules.impl.data.database.migrations.MIGRATION_1_2

@TypeConverters(InstantConverter::class)
@Database(
    entities = [RuleEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class RulesDatabase : RoomDatabase() {

    abstract fun ruleDao(): RuleDao

    companion object {
        @Volatile
        private var INSTANCE: RulesDatabase? = null

        fun getDatabase(context: Context): RulesDatabase {
            // Return the existing instance, or if it's null, create the database.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RulesDatabase::class.java,
                    "dend_rules_database"
                )
//                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
