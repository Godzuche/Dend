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
import kotlinx.coroutines.Job
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
//    private val uiEventBus: UiEventBus,
) : ViewModel() {

    private val eventChannel = Channel<RulesUiEvent>()
    val events = eventChannel.receiveAsFlow()

    private var itemToDelete: MutableStateFlow<Rule?> = MutableStateFlow(null)
//    private var deleteJob: Job? = null

    private val _uiState = MutableStateFlow(RulesUiState())
    val uiState = combine(
        _uiState,
        rulesRepository.blacklist,
        rulesRepository.whitelist,
//        itemToDelete,
    ) { state, blacklist, whitelist/*, deletedItem*/ ->
        state.copy(
            blacklistState = RulesState.Success(
                rules = /*if (deletedItem?.type == RuleType.BLACKLIST) {
                    blacklist.filter { rule ->
                        rule.number != deletedItem.number
                    }
                } else */blacklist,
            ),
            whitelistState = RulesState.Success(
                rules = /*if (deletedItem?.type == RuleType.WHITELIST) {
                    whitelist.filter { rule ->
                        rule.number != deletedItem.number
                    }
                } else */whitelist,
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

        /*deleteJob = */viewModelScope.launch {
//            rulesRepository.removeRule(ruleItem)
//                .onError { error ->
//                    eventChannel.send(
//                        RulesUiEvent.OperationFailed(error)
//                    )
//                }

            rulesRepository.commitDeletion(ruleItem)
                .onError { error ->
                    //
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
//        viewModelScope.launch {
//            if (number == null) {
//                val message = UiText.DynamicString("Cannot add a private number")
//                messenger.showMessage(message)
//                return@launch
//            }

        addRule(
            number = number,
            name = name,
            ruleType = uiState.value.selectedRulesTab,
        )

//    }
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
        // Cancel any pending delete operation
//        deleteJob?.cancel()

        // Hold the item in a temporary variable
        itemToDelete.value = item

        // Optimistically update the UI by removing the item from the list
//        _uiState.update { currentState ->
//            when (item.type) {
//                RuleType.BLACKLIST -> {
//                    val rules = currentState.blacklistState.rules.filter {
//                        it.number != item.number
//                    }
//                    currentState.copy(
//                        blacklistState = RulesState.Success(rules)
//                    )
//                }
//
//                RuleType.WHITELIST -> {
//                    val rules = currentState.whitelistState.rules.filter {
//                        it.number != item.number
//                    }
//                    currentState.copy(
//                        whitelistState = RulesState.Success(rules)
//                    )
//                }
//            }
//        }

        // Send the snackbar event with the Undo action
        viewModelScope.launch {
//            val text =
//                UiText.StringResource(R.string.rule_removed_successfully, listOf(item.number))
//            messenger.sendEvent(UiEvent.ShowSnackbar(text, actionLabelResId = R.string.undo_action))

            // This marks `isPendingDeletion = true` in the DB. The UI updates automatically
            // because the main getRules()  flow filters these out.
            rulesRepository.markItemForDeletion(item)
                .onSuccess {
                    // Send the snackbar event with the Undo action.
//                    val text = UiText.StringResource(
//                        R.string.rule_removed_successfully,
//                        listOf(item.number)
//                    )
//                    messenger.sendEvent(
//                        UiEvent.ShowSnackbar(
//                            text,
//                            actionLabelResId = R.string.undo_action
//                        )
//                    )

                    eventChannel.send(
                        RulesUiEvent.RuleRemoved(
                            item.run { name ?: number },
                            uiState.value.selectedRulesTab,
                        )
                    )
                }
                .onError { error ->
                    // If marking fails, show an error and clear the temp item.
//                    eventChannel.send(UiEvent.ShowSnackbar((error as DataError).toUiText()))
                    eventChannel.send(
                        RulesUiEvent.OperationFailed(error)
                    )
                    itemToDelete.value = null
                }

//            eventChannel.send(
//                RulesUiEvent.RuleRemoved(
//                    item.run { name ?: number },
//                    uiState.value.selectedRulesTab,
//                )
//            )
        }
    }

    /**
     * Called by the UI if the user taps the "Undo" action.
     */
    fun onUndoRemove() {
        // Cancel the pending delete job
//        deleteJob?.cancel()

//        itemToDelete.value?.let { item ->
//            // Re-add the item to the UI state. The database was never touched.
//            _uiState.update { currentState ->
//                when (item.type) {
//                    RuleType.BLACKLIST -> {
//                        val rules =
//                            (currentState.blacklistState.rules + item).sortedByDescending { it.createdAt }
//                        currentState.copy(blacklistState = RulesState.Success(rules))
//                    }
//
//                    RuleType.WHITELIST -> {
//                        val rules =
//                            (currentState.whitelistState.rules + item).sortedByDescending { it.createdAt }
//                        currentState.copy(whitelistState = RulesState.Success(rules))
//                    }
//                }
//            }
//        }

        itemToDelete.value?.let { item ->
            viewModelScope.launch {
                rulesRepository.unmarkItemForDeletion(item)
                    .onError { error ->
//                        messenger.sendEvent(UiEvent.ShowSnackbar((error as DataError).toUiText()))
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
        // This is where we actually delete from the database
//        itemToDelete?.let { item ->
//            deleteJob = viewModelScope.launch {
//                rulesRepository.removeRule(item)
//                itemToDelete = null // Clear after successful deletion
//            }
//        }

        itemToDelete.value?.let {
            onRemoveRule(it)
        }
    }
}
