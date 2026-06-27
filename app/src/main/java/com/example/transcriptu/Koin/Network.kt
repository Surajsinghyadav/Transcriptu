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



val networkModule = module {
    single<Interceptor> {
        Interceptor { chain ->
            val headers = chain.request().newBuilder()
                .addHeader("x-rapidapi-key", "b1c70f9676msh6eefd393576a6cap110c63jsn73aa644130e1")
                .addHeader("x-rapidapi-host", "youtube-transcript3.p.rapidapi.com")
                .build()
            chain.proceed(headers)
        }
    }

    single <HttpLoggingInterceptor>{
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    single<TranscriptuService> {
        Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(get())
            .baseUrl("https://youtube-transcript3.p.rapidapi.com/")
            .build()
            .create(TranscriptuService::class.java)
    }
}
