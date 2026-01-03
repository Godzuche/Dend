package com.godzuche.dend.features.activity.impl.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.godzuche.dend.core.data.database.InstantConverter

@TypeConverters(InstantConverter::class, FirewallStateConverter::class)
@Database(entities = [BlockedCallEntity::class], version = 1, exportSchema = false)
abstract class ActivityDatabase : RoomDatabase() {

    abstract fun blockedCallDao(): BlockedCallDao

    companion object {
        @Volatile
        private var INSTANCE: ActivityDatabase? = null

        fun getDatabase(context: Context): ActivityDatabase {
            // Return the existing instance, or if it's null, create the database.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ActivityDatabase::class.java,
                    "dend_activity_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}