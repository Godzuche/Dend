package com.godzuche.dend.features.firewall.impl.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.features.firewall.impl.presentation.components.StatCard
import com.godzuche.dend.features.firewall.impl.presentation.components.StatusToggleButton

enum class FirewallState {
    OFF,
    ON, // Firewall - Standard protection, blocks blacklist
    ZEN, // ZenMode - Max protection, blocks all except whitelist (VIPs)
}

@Composable
fun DashboardScreen(
    onNavigateToActivity: () -> Unit,
    onNavigateToRules: () -> Unit,
) {
    var firewallState by rememberSaveable { mutableStateOf(FirewallState.ON) }

    // Cycle to the next state on click
    fun cycleState() {
        firewallState = when (firewallState) {
            FirewallState.OFF -> FirewallState.ON
            FirewallState.ON -> FirewallState.ZEN
            FirewallState.ZEN -> FirewallState.OFF
        }
    }

    DashboardScreenContent(
        firewallState = firewallState,
        onToggleStatus = ::cycleState,
        onActivityStatClick = onNavigateToActivity,
        onRulesStatClick = onNavigateToRules,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardScreenContent(
    firewallState: FirewallState,
    onToggleStatus: () -> Unit,
    onActivityStatClick: () -> Unit,
    onRulesStatClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val statsAlpha by animateFloatAsState(
        targetValue = if (firewallState != FirewallState.OFF) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "statsAlpha",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        StatusToggleButton(
            state = firewallState,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggleStatus()
            }
        )

        Spacer(Modifier.height(16.dp))

        val statusText = when (firewallState) {
            FirewallState.OFF -> "Firewall is Off"
            FirewallState.ON -> "Firewall Active"
            FirewallState.ZEN -> "Zen Mode Active"
        }
        Text(
            text = statusText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        val subtitleText = when (firewallState) {
            FirewallState.OFF -> "All calls will ring normally."
            FirewallState.ON -> "Blocking numbers on your blacklist."
            FirewallState.ZEN -> "Blocking all calls except your VIPs."
        }
        Text(
            text = subtitleText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.weight(1.5f))

//        AnimatedVisibility(
//            visible = firewallState != FirewallState.OFF,
//            enter = fadeIn(animationSpec = tween(300)),
//            exit = fadeOut(animationSpec = tween(150))
//        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .alpha(statsAlpha),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Calls Blocked Today",
                value = "17",
                onClick = onActivityStatClick
            )

            StatCard(
                modifier = Modifier.weight(1f),
                label = "Numbers on Blacklist",
                value = "42",
                onClick = onRulesStatClick
            )
        }
//        }

        Spacer(Modifier.height(24.dp))
    }
}


@Preview(showBackground = true, device = "id:pixel_6", name = "Firewall OFF State")
@Composable
private fun DashboardScreenOffPreview() = DendTheme {
    DashboardScreenContent(firewallState = FirewallState.OFF, {}, {}, {})
}

@Preview(showBackground = true, device = "id:pixel_6", name = "Firewall OFF State")
@Composable
private fun DashboardScreenOnPreview() = DendTheme {
    DashboardScreenContent(firewallState = FirewallState.ON, {}, {}, {})
}

@Preview(showBackground = true, device = "id:pixel_6", name = "Firewall OFF State")
@Composable
private fun DashboardScreenZenPreview() = DendTheme {
    DashboardScreenContent(firewallState = FirewallState.ZEN, {}, {}, {})
}
