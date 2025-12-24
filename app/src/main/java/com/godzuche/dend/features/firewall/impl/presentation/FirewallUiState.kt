package com.godzuche.dend.features.firewall.impl.presentation

import androidx.compose.runtime.Stable
import com.godzuche.dend.core.domain.model.FirewallState

@Stable
sealed interface FirewallUiState {
    data object Loading : FirewallUiState
    data class Success(override val firewallState: FirewallState) : FirewallUiState

    val firewallState: FirewallState get() = FirewallState.OFF
}