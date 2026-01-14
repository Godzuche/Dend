package com.godzuche.dend.features.activity.impl.domain.model

import androidx.compose.runtime.Stable
import com.godzuche.dend.core.domain.model.FirewallState
import kotlin.time.Instant

@Stable
data class BlockedCall(
    val id: Int,
    val number: String,
    val name: String?,
    val timestamp: Instant,
    val blockedInMode: FirewallState,
)
