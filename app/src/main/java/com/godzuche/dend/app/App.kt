package com.godzuche.dend.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.godzuche.dend.app.navigation.NavigationRoot
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel
import com.godzuche.dend.ui.theme.DendTheme

@Composable
fun App(
    onRequestRolePermission: () -> Unit,
    onboardingViewModel: OnboardingViewModel, // Todo: use Koin DI
) {
    DendTheme {
        Scaffold { innerPadding ->
            NavigationRoot(
                onboardingViewModel = onboardingViewModel,
                onRequestRolePermission = onRequestRolePermission,
                modifier = Modifier
                    .fillMaxSize()
//                    .padding(innerPadding),
            )
        }
    }
}