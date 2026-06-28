package com.example.transcriptu.Koin

import com.example.transcriptu.data.remote.TranscriptuService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

fun buildOkHttpClient(apiKey: String): OkHttpClient {
    val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("x-rapidapi-key", apiKey)
            .addHeader("x-rapidapi-host", "youtube-transcript3.p.rapidapi.com")
            .build()
        chain.proceed(request)
    }
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    return OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
}

fun buildTranscriptuService(apiKey: String): TranscriptuService {
    return Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(buildOkHttpClient(apiKey))
        .baseUrl("https://youtube-transcript3.p.rapidapi.com/")
        .build()
        .create(TranscriptuService::class.java)
}

val networkModule = module {
    factory<TranscriptuService> { (apiKey: String) ->
        buildTranscriptuService(apiKey)
    }
}