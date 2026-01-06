package com.godzuche.dend.features.rules.impl.domain.model

import kotlin.time.Instant

data class Rule(
//    val id: Int = 0,
    val number: String,
    val name: String?,
    val type: RuleType,
    val createdAt: Instant,
    val isPendingDeletion: Boolean,
) {
    val displayName get() = name ?: number
    val displayNameWithNumber
        get() = if (!name.isNullOrBlank()) {
            "$name ($number)"
        } else number
}
