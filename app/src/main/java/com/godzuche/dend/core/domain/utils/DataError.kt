package com.godzuche.dend.core.domain.utils

import com.godzuche.dend.core.data.utils.NormalizationException

sealed interface DataError : Error {
    sealed interface Remote : DataError {
        data class Unauthorized(val remoteMessage: String? = null) : Remote
        data object RequestTimeout : Remote
        data object TooManyRequests : Remote
        data object NoInternet : Remote
        data object Server : Remote
        data object Serialization : Remote
        data class BadClientRequest(val remoteMessage: String? = null) : Remote
        data object Unknown : Remote
    }

//    enum class Local : DataError {
//        DISK_FULL,
//        UNKNOWN
//    }

    /**
     * Errors related to on-device data operations (database, files, etc.).
     */
    sealed interface Local : DataError {
        /**
         * A failure occurred during a database operation (e.g., SQLiteConstraintException).
         * @param cause The original throwable from the database layer.
         */
        data class DatabaseError(val cause: Throwable? = null) : Local

        /**
         * A failure to parse or normalize a user-provided phone number.
         * @param originalNumber The raw number that failed normalization.
         */
        data class NormalizationError(
            val originalNumber: String,
            val exception: NormalizationException,
        ) : Local
    }

}