package com.transcriptapp.ui.screens

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.transcriptapp.ui.components.*

/**
 * UiState for the transcript detail screen.
 */
data class TranscriptDetailUiState(
    val videoTitle: String = "",
    val channelName: String = "",
    val language: TranscriptLanguage = SupportedLanguages.first(),
    val hasTimestamps: Boolean = true,
    val segments: List<TranscriptSegment> = emptyList(),
    val isLoading: Boolean = false,
    val isCopied: Boolean = false,
)

/**
 * TranscriptDetailScreen — Full transcript viewer.
 *
 * Shows the fetched transcript with segment cards.
 * Users can copy, share, or toggle timestamp visibility.
 * Top bar has a back button to return to HomeScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptDetailScreen(
    uiState: TranscriptDetailUiState,
    onBackClick: () -> Unit,
    onCopyAllClick: () -> Unit,
    onShareClick: () -> Unit,
    onTimestampClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTimestamps by remember { mutableStateOf(uiState.hasTimestamps) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    val filteredSegments = remember(uiState.segments, searchQuery) {
        if (searchQuery.isBlank()) uiState.segments
        else uiState.segments.filter {
            it.text.contains(searchQuery, ignoreCase = true)
        }
    }

    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TranscriptTopBar(
                title = uiState.videoTitle,
                channelName = uiState.channelName,
                onBackClick = onBackClick,
                onCopyClick = onCopyAllClick,
                onShareClick = onShareClick,
                onSearchClick = { showSearchBar = !showSearchBar },
                isCopied = uiState.isCopied
            )
        },
        floatingActionButton = {
            // Scroll to top FAB — visible when scrolled down
            val showFab by remember { derivedStateOf { listState.firstVisibleItemIndex > 3 } }
            AnimatedVisibility(
                visible = showFab,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        // In MVVM layer, call coroutineScope.launch { listState.scrollToItem(0) }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "Scroll to top")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search bar — animated
            AnimatedVisibility(
                visible = showSearchBar,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                AppTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search transcript...",
                    leadingIcon = Icons.Outlined.Search,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    trailingContent = if (searchQuery.isNotEmpty()) {
                        {
                            AppIconButton(
                                icon = Icons.Outlined.Close,
                                contentDescription = "Clear search",
                                onClick = { searchQuery = "" }
                            )
                        }
                    } else null
                )
            }

            // Stats & controls bar
            TranscriptMetaBar(
                language = uiState.language.displayName,
                segmentCount = filteredSegments.size,
                totalCount = uiState.segments.size,
                showTimestamps = showTimestamps,
                onTimestampToggle = { showTimestamps = !showTimestamps }
            )

            if (uiState.isLoading) {
                TranscriptLoadingSkeleton()
            } else if (filteredSegments.isEmpty() && searchQuery.isNotBlank()) {
                EmptySearchState(query = searchQuery)
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 8.dp,
                        end = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = filteredSegments,
                        key = { it.id }
                    ) { segment ->
                        TranscriptSegmentCard(
                            segment = segment,
                            showTimestamp = showTimestamps,
                            onTimestampClick = onTimestampClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TranscriptTopBar(
    title: String,
    channelName: String,
    onBackClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    onSearchClick: () -> Unit,
    isCopied: Boolean,
) {
    TopAppBar(
        navigationIcon = {
            AppIconButton(
                icon = Icons.Outlined.ArrowBack,
                contentDescription = "Go back",
                onClick = onBackClick
            )
        },
        title = {
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (channelName.isNotBlank()) {
                    Text(
                        text = channelName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            AppIconButton(
                icon = Icons.Outlined.Search,
                contentDescription = "Search in transcript",
                onClick = onSearchClick
            )
            AppIconButton(
                icon = if (isCopied) Icons.Outlined.CheckCircle else Icons.Outlined.ContentCopy,
                contentDescription = "Copy all transcript",
                onClick = onCopyClick,
                tint = if (isCopied) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurface
            )
            AppIconButton(
                icon = Icons.Outlined.Share,
                contentDescription = "Share transcript",
                onClick = onShareClick
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun TranscriptMetaBar(
    language: String,
    segmentCount: Int,
    totalCount: Int,
    showTimestamps: Boolean,
    onTimestampToggle: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language badge
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = language,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = if (segmentCount == totalCount) "$totalCount segments"
                           else "$segmentCount / $totalCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Timestamp toggle chip
            FilterChip(
                selected = showTimestamps,
                onClick = onTimestampToggle,
                label = {
                    Text("Timestamps", style = MaterialTheme.typography.labelSmall)
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun TranscriptLoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(8) { index ->
            SkeletonSegmentRow(lineCount = if (index % 3 == 0) 3 else 2)
        }
    }
}

@Composable
private fun SkeletonSegmentRow(lineCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Timestamp skeleton
            ShimmerBox(width = 60.dp, height = 24.dp, shape = MaterialTheme.shapes.extraSmall)
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(lineCount) { i ->
                    ShimmerBox(
                        width = if (i == lineCount - 1)
                            (60..90).random().dp else Dp.Infinity,
                        height = 14.dp,
                        fillMaxWidth = i < lineCount - 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimmerBox(
    width: Dp,
    height: Dp,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    fillMaxWidth: Boolean = false,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    Surface(
        modifier = if (fillMaxWidth) Modifier.fillMaxWidth().height(height)
                   else Modifier.width(width).height(height),
        shape = shape,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.12f)
    ) {}
}

@Composable
private fun EmptySearchState(query: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "No results for \"$query\"",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Try a different keyword",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
