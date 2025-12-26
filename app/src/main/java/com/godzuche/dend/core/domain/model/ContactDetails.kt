package com.godzuche.dend.core.domain.model

/**
 * A data class to hold the resolved contact information.
 * This is a clean way to return multiple values from the function.
 */
data class ContactDetails(
    val name: String,
    val phoneNumber: String
)