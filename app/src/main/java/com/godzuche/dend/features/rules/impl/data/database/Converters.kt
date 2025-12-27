package com.godzuche.dend.features.rules.impl.data.database

import androidx.room.TypeConverter
import kotlin.time.Instant

/**
 * Type converters to allow Room to store and retrieve complex types like Instant.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): kotlin.time.Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun dateToTimestamp(instant: kotlin.time.Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }
}