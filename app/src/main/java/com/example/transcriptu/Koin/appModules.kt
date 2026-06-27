package com.example.transcriptu.Koin

import com.example.transcriptu.MetaDataFetcher
import com.example.transcriptu.TranscriptuRepository
import com.example.transcriptu.presentation.screens.TranscriptuViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModules = module {

    single {
        MetaDataFetcher()
    }
   
    single {
        TranscriptuRepository(get(), get())
    }

    viewModel { TranscriptuViewModel(get()) }
}
