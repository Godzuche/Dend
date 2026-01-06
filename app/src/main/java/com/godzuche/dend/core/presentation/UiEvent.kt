package com.godzuche.dend.core.presentation

sealed interface UiEvent {
    data class ShowSnackbar(
        val message: UiText,
        val snackbarAction: ShowSnackbarAction? = null,
    ) : UiEvent

//    data class SnackbarUndo(val action: SnackbarAction) : UiEvent
}

//object SnackBarActions {
//    const val UNDO = "Undo"
//}

sealed class ShowSnackbarAction(val action: SnackbarAction) {
    data class UndoRemoveRule(
        val undoAction: SnackbarAction
    ) : ShowSnackbarAction(undoAction)
}

enum class SnackbarAction(val label: String) {
    UNDO("Undo"),
}