package com.godzuche.dend.features.main.impl.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.firewall.api.FirewallNavKey
import com.godzuche.dend.features.main.impl.navigation.TOP_LEVEL_MAIN_SCREEN_ROUTES
import com.godzuche.dend.core.designsystem.theme.DendTheme

@Composable
fun DenDNavigationBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    ShortNavigationBar(
        modifier = modifier,
    ) {
        TOP_LEVEL_MAIN_SCREEN_ROUTES.forEach { (topLevelRouteKey, navBarItem) ->
            val isSelected = topLevelRouteKey == selectedKey
            ShortNavigationBarItem(
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

@Preview(showBackground = true)
@Composable
fun DenDNavigationBarPreview() = DendTheme {
    DenDNavigationBar(
        selectedKey = FirewallNavKey,
        onSelectKey = {},
    )
}
