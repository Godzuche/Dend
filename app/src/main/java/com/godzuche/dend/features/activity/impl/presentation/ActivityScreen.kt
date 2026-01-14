package com.godzuche.dend.features.activity.impl.presentation

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godzuche.dend.core.data.utils.PhoneNumberNormalizer
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.features.activity.impl.presentation.components.ActivityListItem2
import com.godzuche.dend.features.activity.impl.presentation.components.DateHeader
import com.godzuche.dend.features.activity.impl.presentation.components.EmptyActivityState
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import com.godzuche.dend.features.rules.impl.presentation.RulesViewModel
import org.koin.compose.viewmodel.koinActivityViewModel
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = koinActivityViewModel(),
    rulesViewModel: RulesViewModel = koinActivityViewModel(),
) {
    val activityUiState by viewModel.activityUiState.collectAsStateWithLifecycle()

    val _expandedItemKey = remember { mutableStateOf<String?>(null) }
    val expandedItemKey = _expandedItemKey

    val context = LocalContext.current

    fun onItemClicked(itemKey: String) {
        _expandedItemKey.value = if (_expandedItemKey.value == itemKey) null else itemKey
    }

    fun onActionCompleted() {
        // Collapse the item after an action is performed
        _expandedItemKey.value = null
    }

    fun onCallNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$number".toUri()
        }
        context.startActivity(intent)
        onActionCompleted()
    }

    fun onAddContact(number: String) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.PHONE, number)
        }
        context.startActivity(intent)
        onActionCompleted()
    }

    ActivityScreenContent(
        uiState = activityUiState,
        onAllowClick = { blockedCall ->
            rulesViewModel.addRule(
                number = blockedCall.number,
                name = blockedCall.name,
                ruleType = RuleType.WHITELIST,
            )
        },
        expandedItemKey = expandedItemKey.value,
        onItemClicked = ::onItemClicked,
        onCallNumber = ::onCallNumber,
        onAddContact = ::onAddContact,
    )


}

@Composable
fun ActivityScreenContent(
    uiState: ActivityUiState,
    expandedItemKey: String?,
    onAllowClick: (BlockedCallItemUiState) -> Unit,
    onItemClicked: (String) -> Unit,
    onCallNumber: (String) -> Unit,
    onAddContact: (String) -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState.blockedCallsUiState) {
            is BlockedCallsState.Loading -> CircularProgressIndicator()

            is BlockedCallsState.Success -> {
                if (uiState.blockedCallsUiState.activityLog.isEmpty()) {
                    EmptyActivityState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        uiState.blockedCallsUiState.activityTimeline.forEach { timelineItem ->
                            when (timelineItem) {
                                is TimelineItem.DateHeader -> {
                                    stickyHeader(
                                        key = timelineItem.key,
                                        contentType = { "header" },
                                    ) {
                                        DateHeader(text = timelineItem.dateString)
                                    }
                                }

                                is TimelineItem.LogEntry -> {
                                    item(
                                        key = timelineItem.key,
                                        contentType = "log_item",
                                    ) {
                                        Column(
                                            modifier = Modifier.animateItem()
                                        ) {
                                            ActivityListItem2(
                                                item = timelineItem.item,
                                                isExpanded = timelineItem.key == expandedItemKey,
                                                onItemClick = { onItemClicked(timelineItem.key) },
                                                onAllowClick = { onAllowClick(timelineItem.item) },
                                                onCallClick = { onCallNumber(timelineItem.item.number) },
                                                onAddContactClick = { onAddContact(timelineItem.item.number) }
                                            )

                                            HorizontalDivider(
                                                modifier = Modifier.padding(start = 40.dp) // Leading icon spacing
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, device = "id:pixel_6")
@Composable
internal fun EmptyActivityScreenPreview() = DendTheme {
    ActivityScreenContent(
        uiState = ActivityUiState(),
        onAllowClick = {},
        expandedItemKey = null,
        onItemClicked = {},
        onCallNumber = {},
        onAddContact = {},
    )
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
internal fun ActivityScreenPreview() = DendTheme {
    val context = LocalContext.current

    ActivityScreenContent(
        uiState = getActivityUiStatePreviewData(context),
        onAllowClick = {},
        expandedItemKey = "log-1",
        onItemClicked = {},
        onCallNumber = {},
        onAddContact = {},
    )
}

private fun getActivityUiStatePreviewData(context: Context): ActivityUiState {
    val activityLogs = (1..15).map {
        BlockedCallItemUiState(
            id = it,
            number = PhoneNumberNormalizer(context)
                .normalize(it.toString().repeat(10))
                .getOrNull() ?: it.toString().repeat(10),
            name = listOf("Name $it", null).random(),
            timestamp = Clock.System.now() - (0..(60 * 24 * 14)).random().minutes,
            blockedInMode = FirewallState.entries.filter {
                it == FirewallState.ZEN || it == FirewallState.ON
            }.random(),
            isWhitelisted = Random.nextBoolean(),
        )
    }

    return ActivityUiState(
        blockedCallsUiState = BlockedCallsState.Success(
            activityLog = activityLogs,
            activityTimeline = activityLogs.groupActivityItemsByDate(),
        )
    )
}
