package com.godzuche.dend.features.firewall.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.domain.model.next
import com.godzuche.dend.core.domain.repository.UserDataRepository
import com.godzuche.dend.features.activity.impl.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val userDataRepository: UserDataRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())

    private val firewallState: Flow<FirewallState> =
        userDataRepository.userPreferencesData
            .map { it.firewallState }

//    val firewallUiState: StateFlow<FirewallUiState> =
//        firewallState
//            .map {
//                FirewallUiState.Success(it)
//            }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5_000),
//                initialValue = FirewallUiState.Loading,
//            )

    val uiState: StateFlow<DashboardUiState> =
        combine(
            _uiState,
            firewallState,
            activityRepository.stats
        ) { state, firewallState, stats ->
            state.copy(
                firewallUiState = FirewallUiState.Success(
                    firewallState = firewallState
                ),
                statsUiState = StatsUiState.Success(
                    stats = stats.toUiState()
                ),
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = _uiState.value,
            )

    fun toggleFirewallState() {
        viewModelScope.launch {
            userDataRepository.setFirewallState(
                firewallState = uiState.value.firewallUiState
                    .firewallState.cycleState()
            )
        }
    }

    private fun FirewallState.cycleState(): FirewallState {
        return this.next()
    }
}
