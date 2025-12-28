package com.godzuche.dend.features.rules.impl.data.repository

import android.util.Log
import com.godzuche.dend.core.data.utils.PhoneNumberNormalizer
import com.godzuche.dend.features.rules.impl.data.database.RuleDao
import com.godzuche.dend.features.rules.impl.data.database.RuleEntity
import com.godzuche.dend.features.rules.impl.data.mappers.toDomainModel
import com.godzuche.dend.features.rules.impl.data.mappers.toEntity
import com.godzuche.dend.features.rules.impl.domain.model.Rule
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import com.godzuche.dend.features.rules.impl.domain.repository.RulesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class RulesRepositoryImpl(
    private val ruleDao: RuleDao,
    private val phoneNumberNormalizer: PhoneNumberNormalizer,
) : RulesRepository {

    override val blacklist: Flow<List<Rule>> =
        ruleDao.getRules(RuleType.BLACKLIST)
            .map { it.map(RuleEntity::toDomainModel) }

    override val whitelist: Flow<List<Rule>> =
        ruleDao.getRules(RuleType.WHITELIST)
            .map { it.map(RuleEntity::toDomainModel) }

    override suspend fun addRule(number: String, name: String?, type: RuleType) {
        phoneNumberNormalizer.normalize(number)
            .onSuccess { normalizedNumber ->
                Log.d("RulesRepository", "Upserting rule for $normalizedNumber")
                val rule = RuleEntity(
                    number = normalizedNumber,
                    name = name,
                    type = type,
                    createdAt = Clock.System.now()
                )
                ruleDao.upsert(rule)
            }
            .onFailure { failure ->
                Log.w("RulesRepository", "Could not add rule for '$number'. Reason: $failure")
//            // Todo: Send Error to the ui
            }
    }

    override suspend fun removeRule(rule: Rule) {
        ruleDao.delete(rule.toEntity())
    }

    override suspend fun isBlacklisted(number: String?): Boolean {
        return number?.let {
            ruleDao.isNumberInBlacklist(it)
        } ?: true // For unknown caller id (block it)
    }

    override suspend fun isWhitelisted(number: String?): Boolean {
        return number?.let {
            ruleDao.isNumberInWhitelist(number)
        } ?: false // For unknown caller id (block it)
    }
}
