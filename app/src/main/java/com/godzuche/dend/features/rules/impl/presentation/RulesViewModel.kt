package com.godzuche.dend.features.rules.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.dend.core.data.PhoneCallDataSource
import com.godzuche.dend.core.domain.utils.onError
import com.godzuche.dend.core.domain.utils.onSuccess
import com.godzuche.dend.core.presentation.messaging.UiEventBus
import com.godzuche.dend.features.rules.impl.domain.model.Rule
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import com.godzuche.dend.features.rules.impl.domain.repository.RulesRepository
import com.godzuche.dend.features.rules.impl.presentation.state.CallLogUiState
import com.godzuche.dend.features.rules.impl.presentation.state.RulesState
import com.godzuche.dend.features.rules.impl.presentation.state.RulesUiState
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
