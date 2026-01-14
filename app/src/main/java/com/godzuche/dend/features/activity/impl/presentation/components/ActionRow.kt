package com.godzuche.dend.features.activity.impl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.godzuche.dend.R
import com.godzuche.dend.features.activity.impl.presentation.BlockedCallItemUiState

/**
 * The row of quick action chips. Uses standard AssistChip.
 */
@Composable
internal fun ActionRow(
    blockedCallItemUiState: BlockedCallItemUiState,
    onAllowClick: () -> Unit,
    onCallClick: () -> Unit,
    onAddContactClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!blockedCallItemUiState.isWhitelisted) {
            QuickActionChip(
                text = "Allow",
                icon = ImageVector.vectorResource(R.drawable.verified_user_24dp),
                onClick = onAllowClick,
            )
        }

//        QuickActionChip(
//            text = "Call Back",
//            icon = ImageVector.vectorResource(R.drawable.call_24dp),
//            onClick = onCallClick,
//        )
//
//        if (!blockedCallItemUiState.isInDeviceContacts) {
//            QuickActionChip(
//                text = "Add Contact",
//                icon = ImageVector.vectorResource(R.drawable.person_add_24dp),
//                onClick = onAddContactClick,
//            )
//        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onCallClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.call_24dp),
                    contentDescription = "Call Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (!blockedCallItemUiState.isInDeviceContacts) {
                VerticalDivider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                )
                IconButton(onClick = onAddContactClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.person_add_24dp),
                        contentDescription = "Add to Contacts",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}