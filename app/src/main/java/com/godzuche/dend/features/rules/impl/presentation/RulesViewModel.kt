package com.godzuche.dend.features.rules.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.dend.core.data.PhoneCallDataSource
import com.godzuche.dend.core.domain.utils.onError
import com.godzuche.dend.core.domain.utils.onSuccess
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
) : ViewModel() {

    private val eventChannel = Channel<RulesUiEvent>()
    val events = eventChannel.receiveAsFlow()

    private var itemToDelete: MutableStateFlow<Rule?> = MutableStateFlow(null)

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

    private fun onRemoveRule(ruleItem: Rule) {
//        viewModelScope.launch {
//            rulesRepository.removeRule(ruleItem)
//                .onSuccess {
//                    eventChannel.send(
//                        RulesUiEvent.RuleRemoved(
//                            ruleItem.run { name ?: number },
//                            uiState.value.selectedRulesTab,
//                        )
//                    )
//                }
//                .onError { error ->
//                    eventChannel.send(
//                        RulesUiEvent.OperationFailed(error)
//                    )
//                }
//        }

        viewModelScope.launch {
            rulesRepository.commitDeletion(ruleItem)
                .onError { error ->
                    eventChannel.send(
                        RulesUiEvent.OperationFailed(error)
                    )
                }

            itemToDelete.value = null // Clear after successful deletion
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
        addRule(
            number = number,
            name = name,
            ruleType = uiState.value.selectedRulesTab,
        )
    }

    fun addRule(
        number: String?,
        name: String?,
        ruleType: RuleType,
    ) {
        viewModelScope.launch {
            rulesRepository.addRule(
                number = number ?: "",
                name = name,
                type = ruleType,
            )
                .onSuccess {
                    eventChannel.send(
                        RulesUiEvent.RuleAdded(
                            name ?: number!!,
                            ruleType,
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


    /**
     * Phase 1 of the remove operation.
     * Removes the item from the UI state and starts the "undo" window.
     */
    fun onRemoveItem(item: Rule) {
        itemToDelete.value = item

        viewModelScope.launch {
            rulesRepository.markItemForDeletion(item)
                .onSuccess {
                    eventChannel.send(
                        RulesUiEvent.RuleRemoved(
                            item.run { name ?: number },
                            uiState.value.selectedRulesTab,
                        )
                    )
                }
                .onError { error ->
                    eventChannel.send(
                        RulesUiEvent.OperationFailed(error)
                    )
                    itemToDelete.value = null
                }
        }
    }

    /**
     * Called by the UI if the user taps the "Undo" action.
     */
    fun onUndoRemove() {
        itemToDelete.value?.let { item ->
            viewModelScope.launch {
                rulesRepository.unmarkItemForDeletion(item)
                    .onError { error ->
                        eventChannel.send(
                            RulesUiEvent.OperationFailed(error)
                        )
                    }
            }

            // Clear the temporary item
            itemToDelete.value = null
        }
    }

    /**
     * Phase 2 of the remove operation. Called by the UI when the snackbar times out.
     * This permanently deletes the item from the database.
     */
    fun onCommitRemove() {
        itemToDelete.value?.let {
            onRemoveRule(it)
        }
    }
}
