package com.example.transcriptu.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.transcriptu.presentation.screens.TranscriptuViewModel
import com.transcriptapp.ui.screens.HomeScreen


@Composable
fun NavDisplay(
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
            NavEntry<HomeScreen> {
                HomeScreen(transcriptuViewModel, Modifier, innerPadding)
            }


        }
    )




}