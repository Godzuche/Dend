package com.godzuche.dend.features.rules.impl.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {

    /**
     * Inserts a new rule into the database. If a rule with the same number
     * already exists, it will be updated.
     */
    @Upsert
    suspend fun upsert(vararg rule: RuleEntity)

    /**
     * Deletes a specific rule from the database.
     */
    @Delete
    suspend fun delete(vararg rule: RuleEntity)

    /**
     * Retrieves all rules of a specific type (e.g., all blacklist items) as a Flow.
     */
    @Query("SELECT * FROM rules WHERE type = :ruleType AND is_pending_deletion = 0 ORDER BY created_at DESC")
    fun getRules(ruleType: RuleType): Flow<List<RuleEntity>>

    /**
     * Updates an item to mark it for pending deletion.
     */
    @Query("UPDATE rules SET is_pending_deletion = 1 WHERE number = :number")
    suspend fun markForDeletion(number: String)

    /**
     * Reverses a pending deletion by un-marking the flag.
     */
    @Query("UPDATE rules SET is_pending_deletion = 0 WHERE number = :number")
    suspend fun unmarkForDeletion(number: String)

    /**
     * Finds and permanently deletes all rules that are marked for deletion.
     */
    @Query("DELETE FROM rules WHERE is_pending_deletion = 1")
    suspend fun deletePending()

    @Query("SELECT COUNT(*) > 0 FROM rules WHERE number = :number AND type = 'BLACKLIST'")
    suspend fun isNumberInBlacklist(number: String): Boolean

    @Query("SELECT COUNT(*) > 0 FROM rules WHERE number = :number AND type = 'WHITELIST'")
    suspend fun isNumberInWhitelist(number: String): Boolean

}
