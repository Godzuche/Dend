package com.godzuche.dend.features.activity.impl.data.database

import androidx.room.TypeConverter
import com.godzuche.dend.core.domain.model.FirewallState

class FirewallStateConverter {
    @TypeConverter
    fun fromFirewallState(state: FirewallState?): String? {
        return state?.name
    }

    @TypeConverter
    fun toFirewallState(name: String?): FirewallState? {
        return name?.let {
            enumValueOf<FirewallState>(it)
        }
    }
}