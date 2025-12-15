package com.godzuche.dend.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.godzuche.dend.app.navigation.NavigationRoot
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel

@Composable
fun App(
    onRequestRolePermission: () -> Unit,
    onboardingViewModel: OnboardingViewModel, // Todo: use Koin DI
) {
    DendTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            NavigationRoot(
                onboardingViewModel = onboardingViewModel,
                onRequestRolePermission = onRequestRolePermission,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}