package com.godzuche.dend.core.data.database

import androidx.room.TypeConverter
import kotlin.time.Instant

class InstantConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let {
            Instant.fromEpochMilliseconds(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }
}