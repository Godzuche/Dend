package com.godzuche.dend.features.activity.impl.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.godzuche.dend.R
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.presentation.utils.toFriendlyString
import com.godzuche.dend.features.activity.impl.presentation.components.EmptyActivityState
import kotlin.random.Random

@Composable
fun ActivityScreenContent2(
    timelineItems: ActivityUiState,
) {
//    val expandedItemKey by viewModel.expandedItemKey.collectAsState()
    val context = LocalContext.current

    if (timelineItems.blockedCallsUiState.activityTimeline.isEmpty()) {
        EmptyActivityState()
    } else {
        val timelineColor = MaterialTheme.colorScheme.outlineVariant

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                // Draw the central timeline line
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val x = 32.dp.toPx() // The horizontal position of the line
                    drawLine(
                        color = timelineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(10f, 20f),
                            0f
                        ) // Dashed line effect
                    )
                },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            timelineItems.blockedCallsUiState.activityTimeline.forEach { timelineItem ->
                when (timelineItem) {
                    is TimelineItem.DateHeader -> {
                        // Headers are not sticky in this design, but part of the flow
                        item(key = timelineItem.key) {
                            DateHeader2(
                                text = timelineItem.dateString,
                                timelineColor = timelineColor
                            )
                        }
                    }

                    is TimelineItem.LogEntry -> {
                        item(key = timelineItem.key) {
                            Column(modifier = Modifier.animateItem()) {
                                ActivityEventCard(
                                    uiState = timelineItem.item,
                                    isExpanded = Random.nextBoolean()/*timelineItem.key == expandedItemKey*/,
                                    onItemClick = { /*viewModel.onItemClicked(timelineItem.key)*/ },
                                    onAllowClick = { /*viewModel.allowNumber(timelineItem.item)*/ },
                                    onCallClick = {
//                                        viewModel.onCallNumber(
//                                            context,
//                                            timelineItem.item.blockedCall.number
//                                        )
                                    },
                                    onAddContactClick = {
//                                        viewModel.onAddContact(
//                                            context,
//                                            timelineItem.item.blockedCall.number
//                                        )
                                    },
                                    timelineColor = timelineColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun DateHeader2(text: String, timelineColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timeline Dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(timelineColor, CircleShape)
        )
        Spacer(Modifier.width(34.dp))
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun ActivityEventCard(
    uiState: BlockedCallItemUiState,
    isExpanded: Boolean,
    onItemClick: () -> Unit,
    onAllowClick: () -> Unit,
    onCallClick: () -> Unit,
    onAddContactClick: () -> Unit,
    timelineColor: Color
) {
    val expressiveCardShape = CutCornerShape(
        topStart = 24.dp,
        bottomEnd = 24.dp
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // The small dot on the timeline for this event
        Box(
            modifier = Modifier
                .padding(top = 20.dp) // Align with the center of the first row in the card
                .size(12.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )

        Spacer(Modifier.width(16.dp))

        Card(
            onClick = onItemClick,
            modifier = Modifier.weight(1f),
            shape = expressiveCardShape, // <-- Use the expressive shape
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val modeIcon = when (uiState.blockedInMode) {
                        FirewallState.ON -> ImageVector.vectorResource(R.drawable.shield_24dp)
                        FirewallState.ZEN -> ImageVector.vectorResource(R.drawable.self_improvement_24dp) // Use Spa instead
                        FirewallState.OFF -> ImageVector.vectorResource(R.drawable.shield_lock_24dp) // Use help
                    }
                    Icon(
                        imageVector = modeIcon,
                        contentDescription = "Block Mode",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = uiState.timestamp.toFriendlyString(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = uiState.name ?: uiState.number,
                    style = MaterialTheme.typography.headlineSmall, // Bolder typography
                    fontWeight = FontWeight.SemiBold
                )

                if (uiState.name != null) {
                    Text(
                        text = uiState.number, // Show number underneath if name exists
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // ... ActionRow composable from previous answer ...
            }
        }
    }
    Spacer(Modifier.height(16.dp)) // Space between cards
}