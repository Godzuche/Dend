package com.godzuche.dend.features.main.impl.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.main.impl.navigation.TOP_LEVEL_MAIN_SCREEN_ROUTES

@Composable
fun DenDNavigationBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar() {
        TOP_LEVEL_MAIN_SCREEN_ROUTES.forEach { (topLevelRouteKey, navBarItem) ->
            val isSelected = topLevelRouteKey == selectedKey
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectKey(topLevelRouteKey) },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(navBarItem.iconRes),
                        contentDescription = navBarItem.title,
                    )
                },
                label = {
                    Text(navBarItem.title)
                },
            )
        }
    }
}