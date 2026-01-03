package com.godzuche.dend.features.activity.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.dend.core.data.PhoneCallDataSource
import com.godzuche.dend.features.activity.impl.data.mappers.toUiModel
import com.godzuche.dend.features.activity.impl.domain.repository.ActivityRepository
import com.godzuche.dend.features.rules.impl.domain.repository.RulesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ActivityViewModel(
    activityRepository: ActivityRepository,
    rulesRepository: RulesRepository,
    private val phoneCallDataSource: PhoneCallDataSource,
) : ViewModel() {

    private val whitelist: Flow<Set<String>> = rulesRepository.whitelist
        .map { rules ->
            rules.map { it.number }.toSet()
        }

    private val _activityUiState = MutableStateFlow(ActivityUiState())

    val activityUiState: StateFlow<ActivityUiState> =
        combine(
            _activityUiState,
            whitelist,
            activityRepository.blockedCalls,
        ) { state, whitelistedNumbers, blockedCalls ->
            val activityLog = blockedCalls.map { log ->
                val contactExists = phoneCallDataSource.findContactName(log.number) != null

                log.toUiModel(
                    isWhitelisted = whitelistedNumbers.contains(log.number),
                    isInDeviceContacts = contactExists,
                )
            }

            state.copy(
                blockedCallsUiState = BlockedCallsState.Success(
                    activityLog = activityLog,
                    activityTimeline = activityLog.groupActivityItemsByDate(),
                ),
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ActivityUiState(),
            )

    // add methods here later, like `clearActivityLog()`
}