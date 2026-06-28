package com.example.transcriptu.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppScreens : NavKey

@Serializable
object HomeScreen : NavKey

@Serializable
object HistoryScreen : NavKey

@Serializable
object TranscriptScreen : NavKey

@Serializable
object SettingsScreen : NavKey