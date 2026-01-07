package com.godzuche.dend.features.activity.impl.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface BlockedCallDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(blockedCall: BlockedCallEntity)

    /**
     * Gets a chronological list of all blocked calls, with the most recent first.
     */
    @Query("SELECT * FROM blocked_calls ORDER BY timestamp DESC")
    fun getAllBlockedCalls(): Flow<List<BlockedCallEntity>>

    /**
     * Gets a real-time count of all calls blocked, regardless of mode.
     */
    @Query("SELECT COUNT(*) FROM blocked_calls")
    fun getTotalBlockedCount(): Flow<Int>

    /**
     * Gets a real-time count of calls blocked specifically by the main firewall.
     */
    @Query("SELECT COUNT(*) FROM blocked_calls WHERE blockedInMode = 'ON'")
    fun getFirewallBlockedCount(): Flow<Int>

    /**
     * Gets a real-time count of calls blocked by Zen mode.
     */
    @Query("SELECT COUNT(*) FROM blocked_calls WHERE blockedInMode = 'ZEN'")
    fun getZenBlockedCount(): Flow<Int>

    /**
     * Gets the timestamp of the most recently blocked call.
     * The Flow will emit a new Instant whenever a more recent call is blocked.
     */
    @Query("SELECT timestamp FROM blocked_calls ORDER BY timestamp DESC LIMIT 1")
    fun getLatestBlockedCallTimestamp(): Flow<Instant?>

    /**
     * Gets a real-time count of calls blocked since a given timestamp.
     * This is used to calculate the number of calls blocked "today".
     *
     * @param startTimeEpochMillis The timestamp for the beginning of today in epoch milliseconds.
     * @return A Flow that emits the count of calls blocked today.
     */
    @Query("SELECT COUNT(*) FROM blocked_calls WHERE timestamp >= :startTimeEpochMillis")
    fun getBlockedCountSince(startTimeEpochMillis: Long): Flow<Int>
}