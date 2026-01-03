package com.godzuche.dend.features.activity.impl.domain.repository

import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.features.activity.impl.domain.model.BlockedCall
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface ActivityRepository {
    val blockedCalls: Flow<List<BlockedCall>>

    suspend fun logBlockActivity(
        number: String,
        name: String?,
        timestamp: Instant,
        firewallState: FirewallState,
    )
}