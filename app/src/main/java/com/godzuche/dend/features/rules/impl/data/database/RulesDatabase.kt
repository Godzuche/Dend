package com.godzuche.dend.features.rules.impl.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(Converters::class)
@Database(entities = [RuleEntity::class], version = 1, exportSchema = false)
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
                    "dend_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
