package com.godzuche.dend.core.domain.model

data class CallLogItem(
    val phoneNumber: String?,
    val formattedDate: String,
    val contactName: String?, // Nullable if the number is not in the user's contacts
    val callType: String // e.g., "Incoming", "Outgoing", "Missed"
) {
    val displayName
        get() = contactName?.takeIf { it.isNotBlank() }
            ?: phoneNumber?.takeIf { it.isNotBlank() }
            ?: "Private number"
}