package com.godzuche.dend.features.main.impl.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.godzuche.dend.features.main.api.navigation.BottomSheetNavKey
import com.godzuche.dend.features.main.impl.presentation.components.AddNumberSheet

@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.bottomSheetEntry(
    onDismiss: () -> Unit,
    onNavigateToCallLog: () -> Unit,
) {
    entry<BottomSheetNavKey>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { key ->
//        ContentBlue(
//            title = "Route id: ${key.bottomSheetContentType}",
//            modifier = Modifier.clip(
//                shape = RoundedCornerShape(16.dp)
//            )
//        )

        AddNumberSheet(
            onDismiss = onDismiss,
//            onAddFromContactsClick = {
//                onDismiss()
//                // Launch contacts intent
//            },
            onAddFromRecentsClick = {
                onDismiss()
                onNavigateToCallLog()
            },
//            onAddManuallyClick = {
//                onDismiss()
//                // Launch Show a dialog to enter number
//            },
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ContentBase(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .clip(RoundedCornerShape(48.dp))
    ) {
        Title(title)
        if (content != null) content()
        if (onNext != null) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onNext
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun ColumnScope.Title(title: String) {
    Text(
        modifier = Modifier
            .padding(24.dp)
            .align(Alignment.CenterHorizontally),
        fontWeight = FontWeight.Bold,
        text = title,
        color = Color.Black,
    )
}

@Composable
fun ContentBlue(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = ContentBase(
    title = title,
    modifier = modifier.background(PastelBlue),
    onNext = onNext,
    content = content
)

val PastelBlue = Color(0xFF9BF6FF)