package com.godzuche.dend.features.rules.impl.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godzuche.dend.R
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.features.rules.impl.domain.model.Rule
import com.godzuche.dend.features.rules.impl.domain.model.RuleType
import com.godzuche.dend.features.rules.impl.presentation.components.AddManuallyDialog
import com.godzuche.dend.features.rules.impl.presentation.components.RuleList
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RulesScreen(
    onAddClick: () -> Unit,
    viewModel: RulesViewModel = koinActivityViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

//    LaunchedEffect(Unit) {
//        viewModel.getRules()
//    }

    RulesScreenContent(
        rulesUiState = uiState,
        onAddClick = onAddClick,
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
    onAddClick: () -> Unit,
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.add_24dp),
                    contentDescription = "Add Number",
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
                RuleType.entries.forEachIndexed { index, tab ->
                    Tab(
//                        selected = pagerState.currentPage == index,
                        selected = rulesUiState.selectedRulesTab == RuleType.entries[index],
                        onClick = {
//                            scope.launch {
//                                pagerState.animateScrollToPage(index)
//                            }
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
    RulesScreen({})
}
