package com.godzuche.dend.features.onboarding.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godzuche.dend.ui.theme.DendTheme

@Composable
fun PermissionScreen(
    onGrantPermissionClick: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800), // Faster fade-in
        label = "permissionAlphaAnimation",
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.weight(1f))

            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Permission Icon",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "One Final Step",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "To act as your firewall, Android requires DenD to be set as the default \"Caller ID & spam app\".",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "This is a secure, offline system feature. Your data never leaves your device.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )


            Spacer(Modifier.weight(1f))

            Button(
                onClick = onGrantPermissionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
                    .height(56.dp),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Text(
                    text = "Set as Default",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
private fun PermissionScreenPreview() = DendTheme {
    PermissionScreen(onGrantPermissionClick = {})
}
