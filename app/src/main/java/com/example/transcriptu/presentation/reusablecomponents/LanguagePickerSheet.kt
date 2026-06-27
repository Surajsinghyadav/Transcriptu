package com.transcriptapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class TranscriptLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
)

val SupportedLanguages = listOf(
    TranscriptLanguage("en", "English", "English"),
    TranscriptLanguage("hi", "Hindi", "हिन्दी"),
    TranscriptLanguage("es", "Spanish", "Español"),
    TranscriptLanguage("fr", "French", "Français"),
    TranscriptLanguage("de", "German", "Deutsch"),
    TranscriptLanguage("pt", "Portuguese", "Português"),
    TranscriptLanguage("ja", "Japanese", "日本語"),
    TranscriptLanguage("ko", "Korean", "한국어"),
    TranscriptLanguage("zh", "Chinese", "中文"),
    TranscriptLanguage("ar", "Arabic", "العربية"),
    TranscriptLanguage("ru", "Russian", "Русский"),
    TranscriptLanguage("it", "Italian", "Italiano"),
    TranscriptLanguage("tr", "Turkish", "Türkçe"),
    TranscriptLanguage("id", "Indonesian", "Bahasa Indonesia"),
    TranscriptLanguage("bn", "Bengali", "বাংলা"),
)

/**
 * Bottom sheet for selecting transcript language.
 * Includes live search filter.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerSheet(
    selectedLanguage: TranscriptLanguage,
    onLanguageSelected: (TranscriptLanguage) -> Unit,
    onDismiss: () -> Unit,
    ) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredLanguages = remember(searchQuery) {
        if (searchQuery.isBlank()) SupportedLanguages
        else SupportedLanguages.filter {
            it.displayName.contains(searchQuery, ignoreCase = true) ||
            it.nativeName.contains(searchQuery, ignoreCase = true) ||
            it.code.contains(searchQuery, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(width = 36.dp, height = 4.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                ) {}
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transcript Language",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                GhostButton(text = "Cancel", onClick = onDismiss)
            }

            Divider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Search
            AppTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search languages...",
                leadingIcon = Icons.Outlined.Search,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )

            // Language list
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredLanguages, key = { it.code }) { language ->
                    LanguageRow(
                        language = language,
                        isSelected = language.code == selectedLanguage.code,
                        onClick = {
                            onLanguageSelected(language)
                            onDismiss()
                        }
                    )
                }
                if (filteredLanguages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No language found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageRow(
    language: TranscriptLanguage,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = language.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = language.nativeName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
