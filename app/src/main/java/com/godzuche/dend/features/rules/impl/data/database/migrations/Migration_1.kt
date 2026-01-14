package com.godzuche.dend.features.rules.impl.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                ALTER TABLE rules ADD COLUMN is_pending_deletion INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
        )

        db.execSQL(
            """
                ALTER TABLE rules RENAME COLUMN isPendingDeletion TO is_pending_deletion
            """.trimIndent()
        )

        db.execSQL(
            """
                ALTER TABLE rules RENAME COLUMN createdAt TO created_at
            """.trimIndent()
        )
    }
}