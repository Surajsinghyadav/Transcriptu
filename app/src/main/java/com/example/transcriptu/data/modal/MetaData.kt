package com.example.transcriptu.data.modal

import kotlinx.serialization.Serializable

@Serializable
data class MetaData(
    val title: String?,
    val description: String?,
    val thumbnailUrl : String?,
    val videoUrl : String?
)
