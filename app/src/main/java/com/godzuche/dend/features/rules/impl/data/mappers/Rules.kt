package com.godzuche.dend.features.rules.impl.data.mappers

import com.godzuche.dend.features.rules.impl.data.database.RuleEntity
import com.godzuche.dend.features.rules.impl.domain.model.Rule

fun RuleEntity.toDomainModel() = Rule(
//    id = id,
    number = number,
    name = name,
    type = type,
    createdAt = createdAt,
    isPendingDeletion = isPendingDeletion,
)

fun Rule.toEntity() = RuleEntity(
//    id = id,
    number = number,
    name = name,
    type = type,
    createdAt = createdAt,
)