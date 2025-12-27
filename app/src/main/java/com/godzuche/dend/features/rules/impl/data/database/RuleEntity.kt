package com.godzuche.dend.features.rules.impl.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Represents a single rule entry in the local Room database.
 *
 * @param number The phone number being targeted by the rule. This should be normalized.
 * @param name An optional user-provided name or label for the number.
 * @param type The type of rule (BLACKLIST or WHITELIST).
 * @param createdAt The creation timestamp.
 */
@Entity(
    tableName = "rules",
//    indices = [Index(value = ["number"], unique = true)],
)
data class RuleEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
    @PrimaryKey
    val number: String,
    val name: String?,
    val type: RuleType,
    val createdAt: Instant = Clock.System.now(),
)