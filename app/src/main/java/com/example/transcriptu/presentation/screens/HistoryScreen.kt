package com.example.transcriptu.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.transcriptapp.ui.components.*

data class HistoryUiState(
    val items: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = false,
)

/**
 * HistoryScreen — Lists all previously fetched transcripts.
 *
 * Each entry shows thumbnail, title, channel, language, segment count, and fetch time.
 * Users can tap to re-open a transcript or swipe/tap delete to remove.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onItemClick: (HistoryItem) -> Unit,
    onDeleteItem: (HistoryItem) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    if (uiState.items.isNotEmpty()) {
                        GhostButton(
                            text = "Clear all",
                            onClick = onClearAll,
                            leadingIcon = Icons.Outlined.DeleteSweep
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->

        when {
            uiState.isLoading -> HistoryLoadingState(
                modifier = Modifier.padding(innerPadding)
            )
            uiState.items.isEmpty() -> HistoryEmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            else -> HistoryList(
                items = uiState.items,
                onItemClick = onItemClick,
                onDeleteItem = onDeleteItem,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun HistoryList(
    items: List<HistoryItem>,
    onItemClick: (HistoryItem) -> Unit,
    onDeleteItem: (HistoryItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Group label (e.g. "Today")
        item {
            Text(
                text = "Recent",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(
            items = items,
            key = { it.id }
        ) { item ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically()
            ) {
                HistoryCard(
                    item = item,
                    onClick = { onItemClick(item) },
                    onDeleteClick = { onDeleteItem(item) }
                )
            }
        }
    }
}

@Composable
private fun HistoryEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "No transcripts yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Transcripts you fetch will appear here so you can revisit them any time.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HistoryLoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(5) {
            HistorySkeletonCard()
        }
    }
}

@Composable
private fun HistorySkeletonCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            SkeletonRect(width = 80.dp, height = 56.dp, shape = MaterialTheme.shapes.small)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonRect(height = 14.dp, fillMaxWidth = true)
                SkeletonRect(width = 120.dp, height = 12.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SkeletonRect(width = 60.dp, height = 20.dp, shape = MaterialTheme.shapes.extraSmall)
                    SkeletonRect(width = 80.dp, height = 20.dp, shape = MaterialTheme.shapes.extraSmall)
                }
            }
        }
    }
}

@Composable
private fun SkeletonRect(
    width: Dp = 0.dp,
    height: Dp,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    fillMaxWidth: Boolean = false,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hist_shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hist_shimmer_alpha"
    )
    Surface(
        modifier = if (fillMaxWidth) Modifier.fillMaxWidth().height(height)
                   else Modifier.width(width).height(height),
        shape = shape,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.12f)
    ) {}
}
