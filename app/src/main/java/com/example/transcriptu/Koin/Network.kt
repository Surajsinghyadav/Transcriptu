package com.example.transcriptu.Koin

import android.net.Network
import com.example.transcriptu.data.remote.TranscriptuService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.core.scope.get
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import retrofit2.Retrofit

val networkModule = module {
    single {
        Interceptor{ chain ->
            val headers = chain.request().newBuilder()
                .addHeader("x-rapidapi-key", "b1c70f9676msh6eefd393576a6cap110c63jsn73aa644130e1")
                .addHeader("x-rapidapi-host", "youtube-transcript3.p.rapidapi.com")
                .build()
            chain.proceed(headers)
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .build()
    }

    single {
        Retrofit.Builder().apply {
            addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .client(get())
                .baseUrl("https://youtube-transcript3.p.rapidapi.com/")
                .build()
                .create(TranscriptuService::class.java)
        }
    }



}