package com.godzuche.dend.features.activity.impl.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.presentation.utils.toFriendlyDateHeader
import kotlin.time.Instant

@Stable
sealed interface BlockedCallsState {
    data object Loading : BlockedCallsState

    @Immutable
    data class Success(
        override val activityLog: List<BlockedCallItemUiState>,
        override val activityTimeline: List<TimelineItem>,
    ) : BlockedCallsState

    val activityLog get() = emptyList<BlockedCallItemUiState>()
    val activityTimeline get() = emptyList<TimelineItem>()
}

@Stable
data class BlockedCallItemUiState(
    val id: Int,
    val number: String,
    val name: String?,
    val timestamp: Instant,
    val blockedInMode: FirewallState,
    val isWhitelisted: Boolean = false,
    val isInDeviceContacts: Boolean = false,
)

/**
 * A sealed interface to represent a unified item in the activity log timeline.
 * It can either be a header that separates dates, or an actual log entry.
 */
sealed interface TimelineItem {
    /**
     * A unique key for use in LazyColumn.
     */
    val key: String

    data class DateHeader(val dateString: String) : TimelineItem {
        override val key: String = dateString
    }

    data class LogEntry(val item: BlockedCallItemUiState) : TimelineItem {
        override val key: String = "log-${item.id}"
    }
}

fun List<BlockedCallItemUiState>.groupActivityItemsByDate(): List<TimelineItem> {
    val timeline = mutableListOf<TimelineItem>()
    // Use a LinkedHashMap to preserve insertion order (most recent first)
    val groupedByDate = groupBy { it.timestamp.toFriendlyDateHeader() }

    for ((dateString, logsOnDate) in groupedByDate) {
        timeline.add(TimelineItem.DateHeader(dateString))
        logsOnDate.forEach { log ->
            timeline.add(TimelineItem.LogEntry(log))
        }
    }
    return timeline
}
