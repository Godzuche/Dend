package com.godzuche.dend.features.rules.impl.presentation.state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.godzuche.dend.features.rules.impl.domain.model.Rule

@Stable
sealed interface RulesState {
    data object Loading : RulesState

    @Immutable
    data class Success(
        override val rules: List<Rule>
    ) : RulesState

    val rules get() = emptyList<Rule>()
}