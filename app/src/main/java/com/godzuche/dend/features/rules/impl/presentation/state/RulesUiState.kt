package com.godzuche.dend.features.rules.impl.presentation.state

import com.godzuche.dend.features.rules.impl.domain.model.RuleType

data class RulesUiState(
    val selectedRulesTab: RuleType = RuleType.BLACKLIST,
    val blacklistState: RulesState = RulesState.Loading,
    val whitelistState: RulesState = RulesState.Loading,
    val callLogUiState: CallLogUiState = CallLogUiState.Loading,
    val showAddManuallyDialog: Boolean = false,
)