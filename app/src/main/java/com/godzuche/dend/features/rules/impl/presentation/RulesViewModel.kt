package com.godzuche.dend.features.rules.impl.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.dend.core.data.PhoneCallDataSource
import com.godzuche.dend.core.domain.model.CallLogItem
import com.godzuche.dend.features.rules.impl.domain.model.ContactItem
import com.godzuche.dend.features.rules.impl.domain.model.RulesTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        val contacts: List<ContactItem>
    ) : RulesState
}

data class RulesUiState(
    val selectedRulesTab: RulesTab = RulesTab.BLACKLIST,
    val blacklistState: RulesState = RulesState.Loading,
    val whitelistState: RulesState = RulesState.Loading,
    val callLogUiState: CallLogUiState = CallLogUiState.Loading
)

class RulesViewModel(
    private val phoneCallDataSource: PhoneCallDataSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RulesUiState())
    val uiState = _uiState.asStateFlow()

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
                    .map { ContactItem(number = it, name = it) }

                delay(500)
                _uiState.update {
                    it.copy(
                        blacklistState = RulesState.Success(dummyBlacklist),
                    )
                }
            }.await()

            async {
                val dummyWhitelist = listOf("(234) 456-7890", "Chief Alex", "(222) 867-5309")
                    .map { ContactItem(number = it, name = it) }

                delay(500)
                _uiState.update {
                    it.copy(
                        whitelistState = RulesState.Success(dummyWhitelist),
                    )
                }
            }.await()
        }
    }

    /**
     * Loads the most recent entries from the device's call log.
     * This operation is performed on the IO dispatcher as it involves a ContentResolver query.
     * Permissions MUST be checked by the UI layer before this method is called.
     */
    @SuppressLint("MissingPermission", "Range")
    fun loadCallLog(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(callLogUiState = CallLogUiState.Loading) }

            val callLogItems = phoneCallDataSource.loadCallLog(limit = 100)

            _uiState.update {
                it.copy(callLogUiState = CallLogUiState.Success(callLogs = callLogItems))
            }
        }
    }

    fun addNumberToList(number: String) {
        if (uiState.value.selectedRulesTab == RulesTab.BLACKLIST) {
            Log.d("RulesViewModel", "Adding $number to blacklist...")
        } else {
            Log.d("RulesViewModel", "Adding $number to whitelist...")
        }

        // Todo: use an event to show a toast for successful addition
    }

    fun onSelectRulesTab(selectedTab: RulesTab) {
        _uiState.update {
            it.copy(
                selectedRulesTab = selectedTab,
            )
        }
    }

    fun onRemoveNumberFromList(contactItem: ContactItem) {
        if (uiState.value.selectedRulesTab == RulesTab.BLACKLIST) {
            Log.d("RulesViewModel", "Removing ${contactItem.number} to blacklist...")
        } else {
            Log.d("RulesViewModel", "Removing ${contactItem.number} to whitelist...")
        }

        // Todo: use an event to show a toast for successful removal
    }
}
