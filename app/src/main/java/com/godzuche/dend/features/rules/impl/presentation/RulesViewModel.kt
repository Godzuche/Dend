package com.godzuche.dend.features.rules.impl.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.dend.core.data.PhoneCallDataSource
import com.godzuche.dend.core.domain.model.CallLogItem
import com.godzuche.dend.features.rules.impl.domain.model.Rule
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import com.godzuche.dend.features.rules.impl.domain.repository.RulesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
) : ViewModel() {

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

    /*    init {
            getRules()
        }

        fun getRules() {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        blacklistState = RulesState.Loading,
                        whitelistState = RulesState.Loading,
                    )
                }

                async {
                    val dummyBlacklist = listOf("(123) 456-7890", "Spam Caller", "(555) 867-5309")
                        .map { ContactDetails(phoneNumber = it, name = it) }

                    delay(500)
                    _uiState.update {
                        it.copy(
                            blacklistState = RulesState.Success(dummyBlacklist),
                        )
                    }
                }.await()

                async {
                    val dummyWhitelist = listOf("(234) 456-7890", "Chief Alex", "(222) 867-5309")
                        .map { ContactDetails(phoneNumber = it, name = it) }

                    delay(500)
                    _uiState.update {
                        it.copy(
                            whitelistState = RulesState.Success(dummyWhitelist),
                        )
                    }
                }.await()
            }
        }*/

    fun loadCallLog() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(callLogUiState = CallLogUiState.Loading) }

            val callLogItems = phoneCallDataSource.loadCallLog(limit = 100)

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
        }
    }

    fun setShowAddManuallyDialogState(shouldShow: Boolean) {
        _uiState.update {
            it.copy(
                showAddManuallyDialog = shouldShow,
            )
        }
    }

    fun addRule(number: String, name: String?) {
        viewModelScope.launch {
            rulesRepository.addRule(
                number = number,
                name = name,
                type = uiState.value.selectedRulesTab,
            )
        }
    }

}
