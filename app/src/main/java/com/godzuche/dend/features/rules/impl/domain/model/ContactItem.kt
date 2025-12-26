package com.godzuche.dend.features.rules.impl.domain.model

data class ContactItem(
    val number: String,
    val name: String?,
) {
    val displayName get() = name ?: number
    val displayNameWithNumber get() = if (!name.isNullOrBlank()) {
        "$name ($number)"
    } else number
}