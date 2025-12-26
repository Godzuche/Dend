package com.godzuche.dend.features.rules.impl.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godzuche.dend.R
import com.godzuche.dend.core.designsystem.theme.DendTheme
import com.godzuche.dend.features.rules.impl.presentation.components.CallLogListItem
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCallLogScreen(
    onNavigateBack: () -> Unit,
    viewModel: RulesViewModel = koinActivityViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadCallLog(context)
    }

    CustomCallLogScreenContent(
        uiState = uiState.callLogUiState,
        onBackClick = onNavigateBack,
        onAddNumberToListClick = viewModel::addContact,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomCallLogScreenContent(
    uiState: CallLogUiState,
    onBackClick: () -> Unit,
    onAddNumberToListClick: (String, String?) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add from Recent Calls") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_back_24dp),
                            contentDescription = "Back",
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is CallLogUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is CallLogUiState.Success -> {
                    if (uiState.callLogs.isEmpty()) {
                        Text("No recent calls found.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(items = uiState.callLogs) { callLog ->
                                println("CallLogItem: $callLog")
                                CallLogListItem(
                                    item = callLog,
                                    onAddClick = {
                                        onAddNumberToListClick(
                                            callLog.phoneNumber,
                                            callLog.contactName,
                                        )
                                        onBackClick() // Navigate back immediately after adding
                                    }
                                )
                                HorizontalDivider()
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
private fun CustomCallLogScreenPreview() = DendTheme {
    CustomCallLogScreen(onNavigateBack = {})
}
