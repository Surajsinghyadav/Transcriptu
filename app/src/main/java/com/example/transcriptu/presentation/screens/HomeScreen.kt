
package com.example.transcriptu.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transcriptapp.ui.components.*
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

enum class TimestampOption { WITH_TIMESTAMPS, WITHOUT_TIMESTAMPS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    transcriptuViewModel: TranscriptuViewModel = koinViewModel(),
    modifier: Modifier,
    goToTranscriptScreen: () -> Unit,
    goToSettings: () -> Unit,
) {
    var showLanguagePicker by remember { mutableStateOf(false) }
    val uiState by transcriptuViewModel.homeUiState.collectAsState()
    val apiKey by transcriptuViewModel.savedApiKey.collectAsStateWithLifecycle(null)
    var showMissingKeyDialog by remember { mutableStateOf(false) }

    if (showMissingKeyDialog) {
        ApiKeyMissingDialog(
            onGoToSettings = {
                goToSettings()
            },
            onDismiss = {
                showMissingKeyDialog = false
            }
        )
    }
    LaunchedEffect(apiKey) {
        delay(30000.milliseconds)
        showMissingKeyDialog = apiKey == null
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HomeTopBar(onSettingsClick = goToSettings)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            HeroSection()
            Spacer(Modifier.height(28.dp))
            InputCard(
                uiState = uiState,
                apiKey = apiKey,
                goToSettings = goToSettings,
                userEvent = transcriptuViewModel::onEvent,
                showLanguagePicker = { showLanguagePicker = true },
                goToTranscriptScreen = goToTranscriptScreen
            )
            Spacer(Modifier.height(24.dp))
            QuickTipsSection()
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showLanguagePicker) {
        LanguagePickerSheet(
            selectedLanguage = uiState.selectedLanguage,
            onLanguageSelected = {
                showLanguagePicker = false
                transcriptuViewModel.onEvent(UserEvent.LanguageSelected(it))
            },
            onDismiss = { showLanguagePicker = false }
        )
    }
}

@Composable
private fun ApiKeyMissingDialog(
    onGoToSettings: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Outlined.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        title = {
            Text("API Key Required", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Text(
                "To fetch YouTube transcripts, you need a free RapidAPI key. Go to Settings to add yours — it only takes a minute.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onGoToSettings,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Go to Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(onSettingsClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = "TranscriptU",
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
                onClick = onSettingsClick
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
    uiState: HomeScreenInputs,
    userEvent: (UserEvent) -> Unit,
    showLanguagePicker: () -> Unit,
    goToTranscriptScreen: () -> Unit,
    apiKey: String?,
    goToSettings: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            AppTextField(
                value = uiState.urlInput,
                onValueChange = { userEvent(UserEvent.UrlInputChanged(it)) },
                placeholder = "https://youtube.com/watch?v=...",
                label = "YouTube URL",
                leadingIcon = Icons.Outlined.Link,
                trailingContent = if (uiState.urlInput.isNotEmpty()) {
                    {
                        AppIconButton(
                            icon = Icons.Outlined.Close,
                            contentDescription = "Clear URL",
                            onClick = { userEvent(UserEvent.UrlInputChanged("")) }
                        )
                    }
                } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { userEvent(UserEvent.FetchTranscriptClicked) })
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))

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
                    onClick = showLanguagePicker,
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Outlined.Translate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Change", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = "Fetch Transcript",
                onClick = {
                    if (!apiKey.isNullOrBlank()){
                        goToTranscriptScreen()
                        userEvent(UserEvent.FetchTranscriptClicked)
                    }else {
                        goToSettings()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
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
        Triple(Icons.AutoMirrored.Outlined.Article, "Get Transcript", "View the full transcript with clickable timestamps"),
    )
    steps.forEachIndexed { index, (icon, title, desc) ->
        StepRow(step = index + 1, icon = icon, title = title, description = desc)
        if (index < steps.lastIndex) Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun StepRow(
    step: Int,
    icon: ImageVector,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
        }
    }
}
