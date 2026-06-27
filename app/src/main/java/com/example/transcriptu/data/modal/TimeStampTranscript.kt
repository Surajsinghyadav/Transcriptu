package com.example.transcriptu.data.modal

import kotlinx.serialization.Serializable

@Serializable
data class TimeStampTranscript(
    val success: Boolean,
    val transcript: List<Transcript>? = null,
    val error: String? = null
)