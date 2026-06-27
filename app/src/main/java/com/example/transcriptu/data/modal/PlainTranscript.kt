package com.example.transcriptu.data.modal

import kotlinx.serialization.Serializable

@Serializable
data class PlainTranscript(
    val success: Boolean,
    val transcript: String
)