package com.example.transcriptu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.transcriptu.Greeting


@Composable
fun NavDisplay(){
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
            NavEntry<HomeScreen>{
                Greeting()

            }
        }
    )




}