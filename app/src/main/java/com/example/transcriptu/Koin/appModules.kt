package com.example.transcriptu.Koin

import com.example.transcriptu.MetaDataFetcher
import com.example.transcriptu.TranscriptuRepository
import com.example.transcriptu.presentation.screens.TranscriptuViewModel
import com.example.transcriptu.data.remote.TranscriptuService
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    single { MetaDataFetcher() }

    single { (apiKey: String) ->
        TranscriptuRepository(buildTranscriptuService(apiKey), get())
    }

    single {
        TranscriptuRepository(buildTranscriptuService(""), get<MetaDataFetcher>())
    }

    viewModel { TranscriptuViewModel(get(),get()) }
}