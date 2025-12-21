package com.godzuche.dend.features.firewall.impl.presentation

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private val firewallState: Flow<FirewallState> =
        userDataRepository.userPreferencesData
            .map { it.firewallState }

    val firewallUiState: StateFlow<FirewallUiState> =
        firewallState
            .map {
                FirewallUiState.Success(it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FirewallUiState.Loading,
            )

    fun toggleFirewallState() {
        viewModelScope.launch {
            userDataRepository.setFirewallState(
                firewallState = firewallUiState.value.firewallState
                    .cycleState()
            )
        }
    }

    private fun FirewallState.cycleState(): FirewallState {
        return when (this) {
            FirewallState.OFF -> FirewallState.ON
            FirewallState.ON -> FirewallState.ZEN
            FirewallState.ZEN -> FirewallState.OFF
        }
    }
}

@Stable
sealed interface FirewallUiState {
    data object Loading : FirewallUiState
    data class Success(override val firewallState: FirewallState) : FirewallUiState

    val firewallState: FirewallState get() = FirewallState.OFF
}