package com.godzuche.dend.features.onboarding.impl.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godzuche.dend.R
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.core.presentation.utils.rememberRoleRequester
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun RolePermissionScreen() {
    val onboardingViewModel = koinActivityViewModel<OnboardingViewModel>()
    val callScreeningRoleRequester = rememberRoleRequester { isGranted ->
        onboardingViewModel.onRolePermissionResult(isGranted)
    }

    RolePermissionScreenContent(
        onGrantPermissionClick = {
            if (callScreeningRoleRequester.isRoleHeld()) {
                onboardingViewModel.onRolePermissionResult(true)
            } else {
                callScreeningRoleRequester.requestRole()
            }
        },
    )

}

@Composable
fun RolePermissionScreenContent(
    onGrantPermissionClick: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.weight(1f))

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.admin_panel_settings_24dp),
                contentDescription = "Permission Icon",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Enable Firewall",
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
                    .height(56.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(contentColor = Color.White),
            ) {
                Text(
                    text = "Set as Default",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
private fun RolePermissionScreenPreview() = DendTheme {
    RolePermissionScreenContent(onGrantPermissionClick = {})
}
