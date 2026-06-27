package com.example.transcriptu.data.modal

import kotlinx.serialization.Serializable

@Serializable
data class Transcript(
    val text: String? = null,
    val duration: String,
    val lang: String,
    val offset: String,
)