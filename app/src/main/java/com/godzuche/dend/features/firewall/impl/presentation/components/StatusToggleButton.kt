package com.godzuche.dend.features.firewall.impl.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.godzuche.dend.R
import com.godzuche.dend.features.firewall.impl.presentation.FirewallState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StatusToggleButton(
    state: FirewallState,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")

    // This scale value will be animated only when the state is ON.
    // When the state is not ON, the target value will be 1f, making it static.
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (state == FirewallState.ON) 1.05f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Animate the border alpha for a softer pulse effect
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (state == FirewallState.ON) 0.7f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Define the UI properties for each state
    val firewallIcon = ImageVector.vectorResource(R.drawable.shield_24dp)
    val firewallOffIcon = ImageVector.vectorResource(R.drawable.remove_moderator_24dp)
    val zenIcon = ImageVector.vectorResource(R.drawable.self_improvement_24dp)
    val inActiveColor = MaterialTheme.colorScheme.outline
    val activeColor = MaterialTheme.colorScheme.primary
    val onActiveColor = MaterialTheme.colorScheme.onPrimary

    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(animationSpec = tween(400)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(400)
            ) togetherWith
                    fadeOut(animationSpec = tween(200))
        },
        modifier = Modifier
            .size(200.dp)
            .scale(pulseScale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
        label = "status_toggle"
    ) { targetState ->
        val (currentIcon, currentColor, currentBackgroundColor) = when (targetState) {
            FirewallState.OFF -> Triple(
                firewallOffIcon,
                inActiveColor,
                Color.Transparent
            )

            FirewallState.ON -> Triple(
                firewallIcon,
                activeColor,
                Color.Transparent
            )

            FirewallState.ZEN -> Triple(
                zenIcon,
                onActiveColor,
                activeColor
            )
        }


        val borderColor = when (targetState) {
            FirewallState.ON -> {
                MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)
            }
            FirewallState.OFF -> {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            }
            else -> {
                MaterialTheme.colorScheme.primary
            }
        }


        val border = BorderStroke(4.dp, borderColor)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(currentBackgroundColor, CircleShape)
                .border(border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = currentIcon,
                contentDescription = "Firewall Status",
                modifier = Modifier.size(80.dp),
                tint = currentColor,
            )
        }
    }
}