package com.example.transcriptu.data.remote

import com.example.transcriptu.data.modal.PlainTranscript
import com.example.transcriptu.data.modal.TimeStampTranscript
import retrofit2.http.GET
import retrofit2.http.Query

interface TranscriptuService {

    @GET("api/transcript-with-url?flat_text=true")
    suspend fun getPlainTranscript(
        @Query("url") videoUrl : String,
        @Query("lang") language: String
    ) : PlainTranscript

    @GET("api/transcript-with-url?flat_text=false")
    suspend fun getTimeStampTranscript(
        @Query("url") videoUrl : String,
        @Query("lang") language: String
    ) : TimeStampTranscript

}