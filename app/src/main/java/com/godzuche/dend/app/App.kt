package com.godzuche.dend.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.godzuche.dend.app.navigation.NavigationRoot
import com.godzuche.dend.core.designsystem.theme.DendTheme

@Composable
fun App(
    shouldHideOnboarding: Boolean,
) {
    DendTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            NavigationRoot(
                shouldHideOnboarding = shouldHideOnboarding,
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
    }
}