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
    @Query("SELECT * FROM rules WHERE type = :ruleType ORDER BY createdAt DESC")
    fun getRules(ruleType: RuleType): Flow<List<RuleEntity>>
}
