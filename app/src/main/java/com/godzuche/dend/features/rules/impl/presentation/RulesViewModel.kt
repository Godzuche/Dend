package com.godzuche.dend.features.rules.impl.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.dend.core.data.PhoneCallDataSource
import com.godzuche.dend.core.domain.model.CallLogItem
import com.godzuche.dend.core.domain.utils.DataError
import com.godzuche.dend.core.domain.utils.onError
import com.godzuche.dend.core.domain.utils.onSuccess
import com.godzuche.dend.core.presentation.messaging.UiEventBus
import com.godzuche.dend.features.rules.impl.domain.model.Rule
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import com.godzuche.dend.features.rules.impl.domain.repository.RulesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface RulesUiEvent {
    data class RuleAdded(
        val number: String,
        val selectedRulesTab: RuleType,
    ) : RulesUiEvent

    data class RuleRemoved(
        val contactLabel: String,
        val selectedRulesTab: RuleType,
    ) : RulesUiEvent

    data class OperationFailed(val error: DataError) : RulesUiEvent
}

@Stable
sealed interface CallLogUiState {
    data object Loading : CallLogUiState

    @Immutable
    data class Success(
        val callLogs: List<CallLogItem>
    ) : CallLogUiState
}

@Stable
sealed interface RulesState {
    data object Loading : RulesState

    @Immutable
    data class Success(
        override val rules: List<Rule>
    ) : RulesState

    val rules get() = emptyList<Rule>()
}

data class RulesUiState(
    val selectedRulesTab: RuleType = RuleType.BLACKLIST,
    val blacklistState: RulesState = RulesState.Loading,
    val whitelistState: RulesState = RulesState.Loading,
    val callLogUiState: CallLogUiState = CallLogUiState.Loading,
    val showAddManuallyDialog: Boolean = false,
)

class RulesViewModel(
    private val phoneCallDataSource: PhoneCallDataSource,
    private val rulesRepository: RulesRepository,
    private val uiEventBus: UiEventBus,
) : ViewModel() {

    private val eventChannel = Channel<RulesUiEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _uiState = MutableStateFlow(RulesUiState())
    val uiState = combine(
        _uiState,
        rulesRepository.blacklist,
        rulesRepository.whitelist,
    ) { state, blacklist, whitelist ->
        state.copy(
            blacklistState = RulesState.Success(
                rules = blacklist,
            ),
            whitelistState = RulesState.Success(
                rules = whitelist,
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _uiState.value,
    )

    fun loadCallLog() {
        viewModelScope.launch {
            _uiState.update { it.copy(callLogUiState = CallLogUiState.Loading) }

            val callLogItems = async(Dispatchers.Default) {
                phoneCallDataSource.loadCallLog(limit = 100)
            }.await()

            _uiState.update {
                it.copy(callLogUiState = CallLogUiState.Success(callLogs = callLogItems))
            }
        }
    }

    fun onSelectRulesTab(selectedTab: RuleType) {
        _uiState.update {
            it.copy(selectedRulesTab = selectedTab)
        }
    }

    fun onRemoveRule(ruleItem: Rule) {
        viewModelScope.launch {
            rulesRepository.removeRule(ruleItem)
                .onSuccess {
                    eventChannel.send(
                        RulesUiEvent.RuleRemoved(
                            ruleItem.run { name ?: number },
                            uiState.value.selectedRulesTab,
                        )
                    )
                }
                .onError { error ->
                    eventChannel.send(
                        RulesUiEvent.OperationFailed(error)
                    )
                }
        }
    }

    fun setShowAddManuallyDialogState(shouldShow: Boolean) {
        _uiState.update {
            it.copy(
                showAddManuallyDialog = shouldShow,
            )
        }
    }

    fun addRule(number: String?, name: String?) {
        viewModelScope.launch {
//            if (number == null) {
//                val message = UiText.DynamicString("Cannot add a private number")
//                messenger.showMessage(message)
//                return@launch
//            }

            rulesRepository.addRule(
                number = number ?: "",
                name = name,
                type = uiState.value.selectedRulesTab,
            )
                .onSuccess {
                    eventChannel.send(
                        RulesUiEvent.RuleAdded(
                            name ?: number!!,
                            uiState.value.selectedRulesTab,
                        )
                    )
                }
                .onError { error ->
                    eventChannel.send(
                        RulesUiEvent.OperationFailed(error)
                    )
                }

        }
    }

}
