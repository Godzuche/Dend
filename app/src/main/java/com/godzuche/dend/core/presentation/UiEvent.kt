package com.godzuche.dend.core.presentation

sealed interface UiEvent {
    data class ShowSnackbar(val message: UiText): UiEvent
}