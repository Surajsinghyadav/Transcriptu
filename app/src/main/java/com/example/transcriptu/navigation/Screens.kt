package com.example.transcriptu.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

sealed interface AppScreens : NavKey

@Serializable
object HomeScreen : NavKey
@Serializable
object HistoryScreen : NavKey
@Serializable
object TranscriptScreen : NavKey