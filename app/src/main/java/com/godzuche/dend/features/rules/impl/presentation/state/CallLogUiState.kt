package com.godzuche.dend.features.rules.impl.presentation.state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.godzuche.dend.core.domain.model.CallLogItem

@Stable
sealed interface CallLogUiState {
    data object Loading : CallLogUiState

    @Immutable
    data class Success(
        val callLogs: List<CallLogItem>
    ) : CallLogUiState
}