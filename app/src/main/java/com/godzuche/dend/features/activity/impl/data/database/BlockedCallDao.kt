package com.godzuche.dend.features.activity.impl.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedCallDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(blockedCall: BlockedCallEntity)

    /**
     * Gets a chronological list of all blocked calls, with the most recent first.
     */
    @Query("SELECT * FROM blocked_calls ORDER BY timestamp DESC")
    fun getAllBlockedCalls(): Flow<List<BlockedCallEntity>>
}