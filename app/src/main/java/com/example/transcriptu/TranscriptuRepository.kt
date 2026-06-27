package com.example.transcriptu

import com.example.transcriptu.data.modal.MetaData
import com.example.transcriptu.data.modal.PlainTranscript
import com.example.transcriptu.data.modal.TimeStampTranscript
import com.example.transcriptu.data.remote.TranscriptuService
import com.transcriptapp.ui.components.TranscriptLanguage


class TranscriptuRepository(val transcriptuService: TranscriptuService, val metaDataFetcher: MetaDataFetcher) {

    suspend fun getMetadata(url: String): MetaData {
        return metaDataFetcher.getMetadata(url)
    }

    suspend fun getPlainTranscript(url: String, language: TranscriptLanguage): PlainTranscript {
        return transcriptuService.getPlainTranscript(url, language.code)
    }

    suspend fun getTimestampTranscript(
        url: String,
        language: TranscriptLanguage
    ): TimeStampTranscript {
        return transcriptuService.getTimeStampTranscript(url, language.code)
    }



}