package com.godzuche.dend.features.activity.impl.data.mappers

import com.godzuche.dend.features.activity.impl.data.database.BlockedCallEntity
import com.godzuche.dend.features.activity.impl.domain.model.BlockedCall
import com.godzuche.dend.features.activity.impl.presentation.BlockedCallItemUiState

fun BlockedCallEntity.toDomainModel() = BlockedCall(
    id = id,
    number = number,
    name = name,
    timestamp = timestamp,
    blockedInMode = blockedInMode,
)

fun BlockedCall.toUiModel(
    isWhitelisted: Boolean,
    isInDeviceContacts: Boolean,
) = BlockedCallItemUiState(
    id = id,
    number = number,
    name = name,
    timestamp = timestamp,
    blockedInMode = blockedInMode,
    isWhitelisted = isWhitelisted,
    isInDeviceContacts = isInDeviceContacts,
)
