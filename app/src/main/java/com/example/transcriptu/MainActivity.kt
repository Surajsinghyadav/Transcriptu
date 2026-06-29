package com.example.transcriptu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.transcriptu.navigation.Transcriptu
import com.example.transcriptu.presentation.screens.TranscriptuViewModel
import com.example.transcriptu.ui.theme.TranscriptuTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transcriptuViewModel : TranscriptuViewModel by viewModel()
        enableEdgeToEdge()
        setContent {
            TranscriptuTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Transcriptu(transcriptuViewModel , innerPadding)
                }
            }
        }
    }
}