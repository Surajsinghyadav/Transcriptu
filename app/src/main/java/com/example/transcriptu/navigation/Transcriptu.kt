package com.example.transcriptu.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.transcriptu.presentation.screens.TranscriptuViewModel
import com.example.transcriptu.presentation.screens.HomeScreen
import com.transcriptapp.ui.screens.TranscriptDetailScreen


@Composable
fun Transcriptu(
    transcriptuViewModel: TranscriptuViewModel,
    innerPadding: PaddingValues
){
    val backStack = rememberNavBackStack(HomeScreen)
    val handleOnBack = {
        if (backStack.size > 1){
            backStack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = handleOnBack,
        entryProvider = entryProvider {
            entry<HomeScreen> {
                HomeScreen(transcriptuViewModel, Modifier, goToTranscriptScreen = {
                    backStack.add(TranscriptScreen)
                })
            }
            entry<TranscriptScreen> {
                TranscriptDetailScreen(
                    transcriptuViewModel = transcriptuViewModel,
                    onBackClick = handleOnBack,
                    onCopyAllClick = {  },
                    onShareClick = {},
                    onTimestampClick ={},
                    modifier = Modifier,
                )
            }



        }
    )




}