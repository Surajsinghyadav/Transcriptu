package com.example.transcriptu.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.transcriptu.presentation.screens.HomeScreen
import com.example.transcriptu.presentation.screens.SettingsScreen
import com.example.transcriptu.presentation.screens.TranscriptuViewModel
import com.example.transcriptu.presentation.screens.TranscriptDetailScreen

@Composable
fun Transcriptu(
    transcriptuViewModel: TranscriptuViewModel,
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(HomeScreen)
    val handleOnBack = {
        if (backStack.size > 1) backStack.removeLastOrNull()
    }

    NavDisplay(
        backStack = backStack,
        onBack = handleOnBack,
        entryProvider = entryProvider {
            entry<HomeScreen> {
                HomeScreen(
                    transcriptuViewModel = transcriptuViewModel,
                    modifier = Modifier,
                    goToTranscriptScreen = { backStack.add(TranscriptScreen) },
                    goToSettings = { backStack.add(SettingsScreen) }
                )
            }
            entry<TranscriptScreen> {
                TranscriptDetailScreen(
                    transcriptuViewModel = transcriptuViewModel,
                    onBackClick = handleOnBack,
                )
            }
            entry<SettingsScreen> {
                SettingsScreen(
                    transcriptuViewModel = transcriptuViewModel,
                    onApiKeySaved = { transcriptuViewModel.saveApiKey(context, it) },
                    onBackClick = handleOnBack
                )
            }
        }
    )
}