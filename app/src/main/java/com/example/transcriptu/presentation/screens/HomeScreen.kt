package com.transcriptapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.transcriptapp.ui.components.*

/**
 * Enum for the two toggle options at the top of the input form.
 */
enum class TimestampOption { WITH_TIMESTAMPS, WITHOUT_TIMESTAMPS }

/**
 * Data holder representing the state surfaced to this screen.
 * (Drive this from your ViewModel in the MVVM layer.)
 */
data class HomeScreenUiState(
    val urlInput: String = "",
    val selectedLanguage: TranscriptLanguage = SupportedLanguages.first(),
    val timestampOption: TimestampOption = TimestampOption.WITHOUT_TIMESTAMPS,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val urlError: String? = null,
)

/**
 * HomeScreen — The main landing screen.
 *
 * Users paste a YouTube URL here, choose language and timestamp preference,
 * then hit "Fetch Transcript".
 *
 * Navigation: onFetchSuccess leads to TranscriptDetailScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeScreenUiState,
    onUrlChange: (String) -> Unit,
    onLanguageChange: (TranscriptLanguage) -> Unit,
    onTimestampToggle: (TimestampOption) -> Unit,
    onFetchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLanguagePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HomeTopBar() }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // Hero section
            HeroSection()

            Spacer(Modifier.height(28.dp))

            // Input card
            InputCard(
                uiState = uiState,
                onUrlChange = onUrlChange,
                onLanguagePickerOpen = { showLanguagePicker = true },
                onTimestampToggle = onTimestampToggle,
                onFetchClick = onFetchClick,
            )

            Spacer(Modifier.height(24.dp))

            // Quick tips section
            QuickTipsSection()

            Spacer(Modifier.height(24.dp))
        }
    }

    // Language picker bottom sheet
    if (showLanguagePicker) {
        LanguagePickerSheet(
            selectedLanguage = uiState.selectedLanguage,
            onLanguageSelected = onLanguageChange,
            onDismiss = { showLanguagePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // App logo mark
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Subtitles,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "TranscriptAI",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        actions = {
            AppIconButton(
                icon = Icons.Outlined.Settings,
                contentDescription = "Settings",
                onClick = { /* Navigate to Settings */ }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun HeroSection() {
    Column {
        Text(
            text = "Extract any\nYouTube transcript",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = MaterialTheme.typography.displayMedium.lineHeight
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Paste a video URL to get a full, readable transcript — with or without timestamps.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InputCard(
    uiState: HomeScreenUiState,
    onUrlChange: (String) -> Unit,
    onLanguagePickerOpen: () -> Unit,
    onTimestampToggle: (TimestampOption) -> Unit,
    onFetchClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // URL field
            AppTextField(
                value = uiState.urlInput,
                onValueChange = onUrlChange,
                placeholder = "https://youtube.com/watch?v=...",
                label = "YouTube URL",
                leadingIcon = Icons.Outlined.Link,
                isError = uiState.urlError != null,
                errorMessage = uiState.urlError,
                trailingContent = if (uiState.urlInput.isNotEmpty()) {
                    {
                        AppIconButton(
                            icon = Icons.Outlined.Close,
                            contentDescription = "Clear URL",
                            onClick = { onUrlChange("") }
                        )
                    }
                } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onFetchClick() })
            )

            Spacer(Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))

            // Language selector row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Language",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = uiState.selectedLanguage.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                OutlinedButton(
                    onClick = onLanguagePickerOpen,
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Translate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Change", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))

            // Timestamp toggle
            Text(
                text = "Include timestamps?",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            TimestampToggle(
                selected = uiState.timestampOption,
                onSelect = onTimestampToggle
            )

            Spacer(Modifier.height(20.dp))

            // Error message
            AnimatedVisibility(visible = uiState.errorMessage != null) {
                uiState.errorMessage?.let {
                    ErrorBanner(message = it)
                    Spacer(Modifier.height(12.dp))
                }
            }

            // CTA button
            PrimaryButton(
                text = "Fetch Transcript",
                onClick = onFetchClick,
                isLoading = uiState.isLoading,
                leadingIcon = if (!uiState.isLoading) Icons.Outlined.Download else null,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )
        }
    }
}

@Composable
private fun TimestampToggle(
    selected: TimestampOption,
    onSelect: (TimestampOption) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimestampOption.values().forEach { option ->
            val isSelected = selected == option
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onSelect(option) }
                    .padding(2.dp),
                shape = MaterialTheme.shapes.medium,
                color = if (isSelected) MaterialTheme.colorScheme.surface
                        else MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = if (isSelected) 2.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (option == TimestampOption.WITH_TIMESTAMPS)
                            Icons.Outlined.AccessTime else Icons.Outlined.TextFields,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (option == TimestampOption.WITH_TIMESTAMPS)
                            "With Timestamps" else "Text Only",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun QuickTipsSection() {
    Text(
        text = "How it works",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(Modifier.height(12.dp))
    val steps = listOf(
        Triple(Icons.Outlined.ContentPaste, "Paste URL", "Copy any YouTube video link and paste it above"),
        Triple(Icons.Outlined.Translate, "Choose Language", "Select the language of the video's transcript"),
        Triple(Icons.Outlined.Article, "Get Transcript", "View the full transcript with clickable timestamps"),
    )
    steps.forEachIndexed { index, (icon, title, desc) ->
        StepRow(step = index + 1, icon = icon, title = title, description = desc)
        if (index < steps.lastIndex) Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun StepRow(
    step: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step number badge
        Surface(
            modifier = Modifier.size(40.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = step.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
