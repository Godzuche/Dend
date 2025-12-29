package com.godzuche.dend.core.presentation.messaging

import com.godzuche.dend.core.presentation.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * A singleton event bus for dispatching user-facing events from anywhere in the app.
 * VewModels or other components can inject this and send messages without needing
 * to know about the UI implementation (Snackbar, Toast, Navigation etc.).
 */
class UiEventBus {
    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    suspend fun sendEvent(message: UiEvent) {
        _events.emit(message)
    }
}
