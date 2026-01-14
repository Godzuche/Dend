package com.godzuche.dend.features.rules.impl.presentation

import com.godzuche.dend.core.domain.utils.DataError
import com.godzuche.dend.features.rules.impl.domain.model.RuleType

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