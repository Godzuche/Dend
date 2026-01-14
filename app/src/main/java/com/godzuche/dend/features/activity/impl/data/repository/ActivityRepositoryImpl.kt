package com.godzuche.dend.features.activity.impl.data.repository

import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.presentation.utils.getStartOfToday
import com.godzuche.dend.features.activity.impl.data.database.BlockedCallDao
import com.godzuche.dend.features.activity.impl.data.database.BlockedCallEntity
import com.godzuche.dend.features.activity.impl.data.mappers.toDomainModel
import com.godzuche.dend.features.activity.impl.domain.model.BlockedCall
import com.godzuche.dend.features.activity.impl.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * A data class to hold the aggregated stats for the dashboard.
 */
data class DashboardStats(
    val totalBlocked: Int = 0,
    val firewallBlocks: Int = 0,
    val zenBlocks: Int = 0,
    val lastBlockedCallTime: Instant? = null,
    val blockedToday: Int = 0,
)

class ActivityRepositoryImpl(
    private val blockedCallDao: BlockedCallDao,
) : ActivityRepository {
    override val blockedCalls: Flow<List<BlockedCall>>
        get() = blockedCallDao.getAllBlockedCalls()
            .map { blockedCalls ->
                blockedCalls.map(BlockedCallEntity::toDomainModel)
            }

    override val stats: Flow<DashboardStats> = combine(
        blockedCallDao.getTotalBlockedCount(),
        blockedCallDao.getFirewallBlockedCount(),
        blockedCallDao.getZenBlockedCount(),
        blockedCallDao.getLatestBlockedCallTimestamp(),
        blockedCallDao.getBlockedCountSince(
            startTimeEpochMillis = getStartOfToday().toEpochMilliseconds()
        )
    ) { total, firewall, zen, lastEvent, todayCount ->
        DashboardStats(
            totalBlocked = total,
            firewallBlocks = firewall,
            zenBlocks = zen,
            lastBlockedCallTime = lastEvent,
            blockedToday = todayCount,
        )
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