package com.godzuche.dend.features.rules.impl.domain.repository

import com.godzuche.dend.features.rules.impl.domain.model.Rule
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides a clean API for accessing rules data.
 */
interface RulesRepository {
    val blacklist: Flow<List<Rule>>
    val whitelist: Flow<List<Rule>>

    /**
     * Adds a new rule to the database.
     */
    suspend fun addRule(number: String, name: String?, type: RuleType)

    /**
     * Removes a rule from the database.
     */
    suspend fun removeRule(rule: Rule)
}