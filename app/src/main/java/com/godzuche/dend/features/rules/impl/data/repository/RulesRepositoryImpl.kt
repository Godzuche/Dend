package com.godzuche.dend.features.rules.impl.data.repository

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
    private val ruleDao: RuleDao
) : RulesRepository {

    override val blacklist: Flow<List<Rule>> =
        ruleDao.getRules(RuleType.BLACKLIST)
            .map { it.map(RuleEntity::toDomainModel) }

    override val whitelist: Flow<List<Rule>> =
        ruleDao.getRules(RuleType.WHITELIST)
            .map { it.map(RuleEntity::toDomainModel) }

    override suspend fun addRule(number: String, name: String?, type: RuleType) {
        // TODO: Add number normalization here (strip non-digits)
        val normalizedNumber = number
        ruleDao.upsert(
            RuleEntity(
                number = normalizedNumber,
                name = name,
                type = type,
                createdAt = Clock.System.now(),
            )
        )
    }

    override suspend fun removeRule(rule: Rule) {
        ruleDao.delete(rule.toEntity())
    }
}
