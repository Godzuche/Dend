package com.godzuche.dend.core.presentation.utils

import com.godzuche.dend.R
import com.godzuche.dend.core.data.utils.NormalizationException
import com.godzuche.dend.core.domain.utils.DataError
import com.godzuche.dend.core.presentation.UiText

/**
 * An extension function that maps a DataError to a user-friendly, displayable UiText object.
 * This is the central point for converting back-end errors into front-end messages.
 */
fun DataError.toUiText(): UiText {
    return when (this) {
        is DataError.Local.DatabaseError -> {
            UiText.StringResource(id = R.string.error_database)
        }

        is DataError.Local.NormalizationError -> {
            when (this.exception) {
                NormalizationException.BlankNumber -> {
                    UiText.DynamicString(
                        value = "Cannot add an empty or private number"
                    )
                }

                is NormalizationException.InvalidNumber -> {
                    UiText.StringResource(
                        id = R.string.error_invalid_number,
                        args = listOf(originalNumber)
                    )
                }

//                null -> {}
            }
        }

        is DataError.Remote.NoInternet -> UiText.StringResource(id = R.string.error_no_internet)
        is DataError.Remote.Server -> UiText.StringResource(id = R.string.error_server)
        else -> UiText.StringResource(id = R.string.error_unknown)
    }
}