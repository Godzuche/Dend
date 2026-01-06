package com.godzuche.dend.features.activity.impl.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godzuche.dend.R
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.presentation.utils.toFriendlyStringTimeOnly
import com.godzuche.dend.features.activity.impl.presentation.BlockedCallItemUiState
import kotlin.time.Clock

@Composable
internal fun ActivityListItem(
    item: BlockedCallItemUiState,
    onAllowClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val modeIcon = when (item.blockedInMode) {
            FirewallState.ON -> ImageVector.vectorResource(R.drawable.shield_24dp)
            FirewallState.ZEN -> ImageVector.vectorResource(R.drawable.self_improvement_24dp)
            FirewallState.OFF -> null
        }
        val iconTint = if (item.blockedInMode == FirewallState.ON) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        }

        if (modeIcon != null) {
            Icon(
                imageVector = modeIcon,
                contentDescription = "Blocked in ${item.blockedInMode.name} mode",
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name ?: item.number,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Blocked • ${item.timestamp.toFriendlyStringTimeOnly()}",
//                style = MaterialTheme.typography.bodyMedium,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (item.isWhitelisted) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.check_circle_24dp),
                    contentDescription = "Allowed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Allowed",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            TextButton(onClick = onAllowClick) {
                Text("Allow")
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun ActivityListItemPreview1() = DendTheme {
    ActivityListItem(
        item = BlockedCallItemUiState(
            id = 1,
            number = "+2348059062696",
            name = "Godzuche",
            timestamp = Clock.System.now(),
            blockedInMode = FirewallState.ON,
            isWhitelisted = false,
        ),
        onAllowClick = {},
    )
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun ActivityListItemPreview2() = DendTheme {
//    ActivityListItem(
//        item = BlockedCallItemUiState(
//            id = 1,
//            number = "+2348059062696",
//            name = "Godzuche",
//            timestamp = Clock.System.now(),
//            blockedInMode = FirewallState.ZEN,
//            isWhitelisted = true,
//        ),
//        onAllowClick = {},
//    )

    ActivityListItem2(
        item = BlockedCallItemUiState(
            id = 1,
            number = "+2348059062696",
            name = "Godzuche",
            timestamp = Clock.System.now(),
            blockedInMode = FirewallState.ZEN,
            isWhitelisted = true,
        ),
        isExpanded = true,
        onItemClick = { },
        onAllowClick = {},
        onCallClick = {},
        onAddContactClick = {},
    )
}


/**
 * The list item representing a single blocked call.
 */
@Composable
fun ActivityListItem2(
    item: BlockedCallItemUiState,
    isExpanded: Boolean,
    onItemClick: () -> Unit,
    onAllowClick: () -> Unit,
    onCallClick: () -> Unit,
    onAddContactClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(vertical = 8.dp)
            .animateContentSize(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium,
                )
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val modeIcon = when (item.blockedInMode) {
                FirewallState.ON -> ImageVector.vectorResource(R.drawable.shield_24dp)
                FirewallState.ZEN -> ImageVector.vectorResource(R.drawable.spa_24dp)
                else -> ImageVector.vectorResource(R.drawable.help_24dp)
            }
            val iconTint = if (item.blockedInMode == FirewallState.ON) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }

            Icon(
                imageVector = modeIcon,
                contentDescription = "Block Mode",
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name ?: item.number,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (item.name != null) {
                    Text(
                        text = item.number,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = item.timestamp.toFriendlyStringTimeOnly(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

        }

        if (isExpanded) {
            Spacer(Modifier.height(8.dp))
            ActionRow(
                blockedCallItemUiState = item,
                onAllowClick = onAllowClick,
                onCallClick = onCallClick,
                onAddContactClick = onAddContactClick,
            )
        }
    }
}
