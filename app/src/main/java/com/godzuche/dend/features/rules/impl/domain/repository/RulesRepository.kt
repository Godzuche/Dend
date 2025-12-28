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

    /**
     * Checks if a normalized phone number is present in the blacklist.
     *
     * @param number The normalized phone number to check.
     * @return True if the number is on the blacklist, false otherwise.
     */
    suspend fun isBlacklisted(number: String?): Boolean

    /**
     * Checks if a normalized phone number is present in the whitelist.
     *
     * @param number The normalized phone number to check.
     * @return True if the number is on the whitelist, false otherwise.
     */
    suspend fun isWhitelisted(number: String?): Boolean
}