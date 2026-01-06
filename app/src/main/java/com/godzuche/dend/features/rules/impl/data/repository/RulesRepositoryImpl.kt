package com.godzuche.dend.features.rules.impl.data.repository

import android.database.sqlite.SQLiteException
import android.util.Log
import com.godzuche.dend.core.data.utils.NormalizationException
import com.godzuche.dend.core.data.utils.PhoneNumberNormalizer
import com.godzuche.dend.core.domain.utils.DataError
import com.godzuche.dend.core.domain.utils.Result
import com.godzuche.dend.features.rules.impl.data.database.RuleDao
import com.godzuche.dend.features.rules.impl.data.database.RuleEntity
import com.godzuche.dend.features.rules.impl.data.mappers.toDomainModel
import com.godzuche.dend.features.rules.impl.data.mappers.toEntity
import com.godzuche.dend.features.rules.impl.domain.model.Rule
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import com.godzuche.dend.features.rules.impl.domain.repository.RulesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Clock

class RulesRepositoryImpl(
    private val ruleDao: RuleDao,
    private val phoneNumberNormalizer: PhoneNumberNormalizer,
) : RulesRepository {

    init {
        // --- Clean up on startup ---
        // Todo: replace with work manager
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("RulesRepository", "Performing startup cleanup of pending deletions.")
            ruleDao.deletePending()
        }
    }

    override val blacklist: Flow<List<Rule>> =
        ruleDao.getRules(RuleType.BLACKLIST)
            .map { blacklist ->
                blacklist.map(RuleEntity::toDomainModel)
            }

    override val whitelist: Flow<List<Rule>> =
        ruleDao.getRules(RuleType.WHITELIST)
            .map { whitelist ->
                whitelist.map(RuleEntity::toDomainModel)
            }

    override suspend fun markItemForDeletion(item: Rule): Result<Unit, DataError.Local> {
//        ruleDao.markForDeletion(item.number)
        return try {
            ruleDao.markForDeletion(item.number)
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DatabaseError(e))
        }
    }

    override suspend fun unmarkItemForDeletion(item: Rule): Result<Unit, DataError.Local> {
//        ruleDao.unmarkForDeletion(item.number)
        return try {
            ruleDao.unmarkForDeletion(item.number)
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DatabaseError(e))
        }
    }

    override suspend fun commitDeletion(item: Rule): Result<Unit, DataError.Local> {
//        ruleDao.deletePending()
        return try {
            ruleDao.deletePending()
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DatabaseError(e))
        }
    }

    override suspend fun addRule(
        number: String,
        name: String?,
        type: RuleType,
    ): Result<Unit, DataError.Local> {
        val normalizationResult = phoneNumberNormalizer.normalize(number)

        val normalizedNumber = normalizationResult.getOrNull()
            ?: return Result.Error(
                DataError.Local.NormalizationError(
                    originalNumber = number,
                    exception = normalizationResult.exceptionOrNull()!! as NormalizationException,
                )
            )

        return try {
            Log.d("RulesRepository", "Upserting rule for $normalizedNumber")
            val rule = RuleEntity(
                number = normalizedNumber,
                name = name,
                type = type,
                createdAt = Clock.System.now(),
                isPendingDeletion = false,
            )
            ruleDao.upsert(rule)
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Log.e("RulesRepository", "Database error while adding rule", e)
            Result.Error(DataError.Local.DatabaseError(e))
        }

    }

    override suspend fun removeRule(rule: Rule): Result<Unit, DataError.Local> {
        return try {
            ruleDao.delete(rule.toEntity())
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Log.e("RulesRepository", "Database error while removing rule", e)
            Result.Error(DataError.Local.DatabaseError(e))
        }
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
