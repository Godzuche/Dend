package com.godzuche.dend.core.domain.model

enum class FirewallState {
    OFF,
    ON, // Firewall - Standard protection, blocks blacklist
    ZEN, // ZenMode - Max protection, blocks all except whitelist (VIPs)
}