package com.godzuche.dend.features.rules.impl.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godzuche.dend.R
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.core.presentation.UiEvent
import com.godzuche.dend.core.presentation.UiText
import com.godzuche.dend.core.presentation.messaging.UiEventBus
import com.godzuche.dend.core.presentation.utils.ObserveAsEvent
import com.godzuche.dend.core.presentation.utils.toUiText
import com.godzuche.dend.features.rules.impl.domain.model.Rule
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import com.godzuche.dend.features.rules.impl.presentation.components.AddManuallyDialog
import com.godzuche.dend.features.rules.impl.presentation.components.RuleList
import com.godzuche.dend.features.rules.impl.presentation.state.RulesUiState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RulesScreen(
    viewModel: RulesViewModel = koinActivityViewModel(),
    uiEventBus: UiEventBus = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvent(
        flow = viewModel.events,
    ) { event ->
        val uiEvent = when (event) {
            is RulesUiEvent.RuleAdded -> {
                val text =
                    UiText.StringResource(
                        R.string.rule_added_successfully,
                        listOf(
                            event.number,
                            event.selectedRulesTab.title,
                        )
                    )
                UiEvent.ShowSnackbar(text)
            }

            is RulesUiEvent.RuleRemoved -> {
                val text =
                    UiText.StringResource(
                        R.string.rule_removed_successfully,
                        listOf(
                            event.contactLabel,
                            event.selectedRulesTab.title,
                        ),
                    )
                UiEvent.ShowSnackbar(text)
            }

            is RulesUiEvent.OperationFailed -> {
                val text = event.error.toUiText()
                UiEvent.ShowSnackbar(text)
            }
        }

        uiEventBus.sendEvent(uiEvent)

    }

    RulesScreenContent(
        rulesUiState = uiState,
        onSelectTab = viewModel::onSelectRulesTab,
        onRemoveNumberClick = viewModel::onRemoveRule,
        onDismissAddManuallyDialog = {
            viewModel.setShowAddManuallyDialogState(false)
        },
        onAddManually = viewModel::addRule,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RulesScreenContent(
    rulesUiState: RulesUiState,
    onSelectTab: (RuleType) -> Unit,
    onRemoveNumberClick: (Rule) -> Unit,
    onDismissAddManuallyDialog: () -> Unit,
    onAddManually: (String, String?) -> Unit,
) {
    val selectedTabIndex = remember(rulesUiState.selectedRulesTab) {
        RuleType.entries.indexOf(rulesUiState.selectedRulesTab)
    }
    val pagerState = rememberPagerState(
        initialPage = RuleType.entries.indexOf(rulesUiState.selectedRulesTab),
    ) { RuleType.entries.size }
    val scope = rememberCoroutineScope()

    // When the selectedTabIndex in the ViewModel changes, scroll the pager.
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex != pagerState.currentPage) {
            scope.launch {
                pagerState.animateScrollToPage(selectedTabIndex)
            }
        }
    }

    // When the user swipes the pager, update the ViewModel.
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            onSelectTab(RuleType.entries[pagerState.currentPage])
        }
    }


    if (rulesUiState.showAddManuallyDialog) {
        AddManuallyDialog(
            onDismissRequest = onDismissAddManuallyDialog,
            onConfirm = { number, name ->
                onAddManually(number, name)
                onDismissAddManuallyDialog()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
            RuleType.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = rulesUiState.selectedRulesTab == RuleType.entries[index],
                    onClick = {
                        onSelectTab(RuleType.entries[index])
                    },
                    text = {
                        Text(tab.title)
                    },
                )
            }
        }

        RulesScreenPager(pagerState) { entry ->
            when (entry) {
                RuleType.BLACKLIST -> {
                    RuleList(
                        rulesState = rulesUiState.blacklistState,
                        listType = entry.title,
                        onRemoveNumberClick = onRemoveNumberClick,
                    )
                }

                RuleType.WHITELIST -> {
                    RuleList(
                        rulesState = rulesUiState.whitelistState,
                        listType = entry.title,
                        onRemoveNumberClick = onRemoveNumberClick,
                    )
                }
            }
        }
    }
}

@Composable
fun RulesScreenPager(
    pagerState: PagerState,
    pageContent: @Composable (RuleType) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
    ) { page ->
        val entry = RuleType.entries[page]
        pageContent(entry)
    }
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
private fun RulesScreenPreview() = DendTheme {
    RulesScreenContent(
        rulesUiState = RulesUiState(),
        onSelectTab = {},
        onRemoveNumberClick = {},
        onDismissAddManuallyDialog = {},
        onAddManually = { _, _ -> },
    )
}
