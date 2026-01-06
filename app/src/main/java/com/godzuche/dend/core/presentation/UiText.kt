package com.godzuche.dend.core.presentation

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    data class StringResource(
        @param: StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : UiText

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(
                id = id,
                formatArgs = args.toTypedArray(),
            )
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> this.value
            is StringResource -> context.getString(
                id,
                *args.toTypedArray(),
            )
        }
    }
}
