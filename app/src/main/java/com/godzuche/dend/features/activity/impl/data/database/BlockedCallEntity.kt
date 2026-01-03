package com.godzuche.dend.features.activity.impl.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.godzuche.dend.core.domain.model.FirewallState
import kotlin.time.Instant

/**
 * Represents a record of a single call that was blocked by the app.
 * This entity is used to create the Activity/History log.
 */
@Entity(tableName = "blocked_calls")
data class BlockedCallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val number: String,
    val name: String?,
    val timestamp: Instant,
    val blockedInMode: FirewallState,
)