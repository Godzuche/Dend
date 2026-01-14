package com.godzuche.dend.core.domain.model

enum class FirewallState {
    OFF,
    ON, // Firewall - Standard protection, blocks blacklist
    ZEN, // ZenMode - Max protection, blocks all except whitelist (VIPs)
}

fun FirewallState.next() = when (this) {
    FirewallState.OFF -> FirewallState.ON
    FirewallState.ON -> FirewallState.ZEN
    FirewallState.ZEN -> FirewallState.OFF
}