package com.godzuche.dend.features.activity.impl.data.repository

import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.features.activity.impl.data.database.BlockedCallDao
import com.godzuche.dend.features.activity.impl.data.database.BlockedCallEntity
import com.godzuche.dend.features.activity.impl.data.mappers.toDomainModel
import com.godzuche.dend.features.activity.impl.domain.model.BlockedCall
import com.godzuche.dend.features.activity.impl.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.Instant

class ActivityRepositoryImpl(
    private val blockedCallDao: BlockedCallDao,
) : ActivityRepository {
    override val blockedCalls: Flow<List<BlockedCall>>
        get() = blockedCallDao.getAllBlockedCalls()
            .map { blockedCalls ->
                blockedCalls.map(BlockedCallEntity::toDomainModel)
            }

    override suspend fun logBlockActivity(
        number: String,
        name: String?,
        timestamp: Instant,
        firewallState: FirewallState,
    ) {
        blockedCallDao.insert(
            BlockedCallEntity(
                number = number,
                name = name,
                timestamp = Clock.System.now(),
                blockedInMode = firewallState,
            )
        )
    }
}