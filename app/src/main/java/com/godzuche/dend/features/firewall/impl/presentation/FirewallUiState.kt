package com.godzuche.dend.features.firewall.impl.presentation

import androidx.compose.runtime.Stable
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.presentation.utils.toFriendlyString
import com.godzuche.dend.features.activity.impl.data.repository.DashboardStats

@Stable
sealed interface FirewallUiState {
    data object Loading : FirewallUiState
    data class Success(override val firewallState: FirewallState) : FirewallUiState

    val firewallState: FirewallState get() = FirewallState.OFF
}

sealed interface StatsUiState {
    data object Loading: StatsUiState

    data class Success(override val stats: DashboardStatsUiState): StatsUiState

    val stats: DashboardStatsUiState get() = DashboardStats().toUiState()
}

data class DashboardUiState(
    val firewallUiState: FirewallUiState = FirewallUiState.Loading,
    val statsUiState: StatsUiState = StatsUiState.Loading,
) {
    val isLoading: Boolean get() = firewallUiState is FirewallUiState.Loading || statsUiState is StatsUiState.Loading
}

data class DashboardStatsUiState(
    val totalBlocked: String,
    val firewallBlocks: String,
    val zenBlocks: String,
    val lastBlockedCallTime: String?,
    val callsBlockedToday: String,
)

fun DashboardStats.toUiState() = DashboardStatsUiState(
    totalBlocked = totalBlocked.toString(),
    firewallBlocks = firewallBlocks.toString(),
    zenBlocks = zenBlocks.toString(),
    lastBlockedCallTime = lastBlockedCallTime?.toFriendlyString(),
    callsBlockedToday = blockedToday.toString(),
)