package com.godzuche.dend.core.domain.model

data class ContactDetails(
    val phoneNumber: String,
    val name: String?,
) {
    val displayName get() = name ?: phoneNumber
    val displayNameWithNumber
        get() = if (!name.isNullOrBlank()) {
            "$name ($phoneNumber)"
        } else phoneNumber
}