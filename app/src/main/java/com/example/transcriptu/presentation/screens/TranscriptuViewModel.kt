package com.example.transcriptu.presentation.screens

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcriptu.TranscriptuRepository
import com.example.transcriptu.data.modal.TimeStampTranscript
import com.example.transcriptu.presentation.screens.TranscriptNetworkState.*
import com.transcriptapp.ui.components.SupportedLanguages
import com.transcriptapp.ui.components.TranscriptLanguage
import com.transcriptapp.ui.screens.TranscriptDetailUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class TranscriptuViewModel(val repository: TranscriptuRepository) : ViewModel() {
    private val _networkUiState = MutableStateFlow<TranscriptNetworkState>(TranscriptNetworkState.Loading)

    val networkUiState = _networkUiState.asStateFlow()

    private val _homeUiState = MutableStateFlow(HomeScreenInputs())
    val homeUiState = _homeUiState.asStateFlow()

    private val _transcriptDetailUiState = MutableStateFlow<TranscriptDetailUiState>(
        TranscriptDetailUiState())

    val transcriptDetailUiState = _transcriptDetailUiState.asStateFlow()



    fun onEvent(event: UserEvent){
        when(event){
            is UserEvent.UrlInputChanged -> {
                _homeUiState.update { it.copy(urlInput = event.url) }
            }

            UserEvent.FetchTranscriptClicked -> {
                viewModelScope.launch {

                    _networkUiState.update { Loading }
                    _transcriptDetailUiState.update { TranscriptDetailUiState()
                        }

                    val url = _homeUiState.value.urlInput
                    val language = _homeUiState.value.selectedLanguage

                    supervisorScope {
                        val metadataAsync = async { repository.getMetadata(url) }
                        val asyncTranscript =
                            async { repository.getTimestampTranscript(url, language) }

                        try {
                            val metaData = metadataAsync.await()
                            metaData.let { result ->
                                _transcriptDetailUiState.update {
                                    it.copy(
                                        videoTitle = result.title ?: "Null",
                                        description = result.description ?: "Null",
                                        thumbnailUrl = result.thumbnailUrl,
                                        videoUrl = result.videoUrl ?: "Null"
                                    )
                                }
                                Log.e("discription ","${result.description}")
                            }
                            _networkUiState.update {
                                Success(
                                    null

                                )
                            }
                        } catch (e: Exception) {
                            _networkUiState.update { Error(error = "Failed to fetch details") }

                        }

                        try {
                            val transcript = asyncTranscript.await()
                            transcript.let { result ->
                                if (result.success && result.transcript != null) {
                                    _transcriptDetailUiState.update { uiState ->
                                        val detectedLang = result.transcript.first()?.lang
                                        uiState.copy(
                                            transcriptSegment = result.transcript,
                                            language = SupportedLanguages.find {
                                                it.code == detectedLang
                                            } ?: SupportedLanguages.first(),
                                            isLoading = false
                                        )
                                    }
                                } else {
                                    _transcriptDetailUiState.update {
                                        it.copy(
                                            isLoading = false,
                                            isTranscriptNotAvailable = true,
                                            apiErrorMessage =result.error ?: "Failed to fetch the Transcript"
                                        )
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            _networkUiState.update {
                                Error(
                                    error = e.localizedMessage ?: "Failed to fetch the Transcript"

                                )
                            }


                        }
                    }

                }


            }
            is UserEvent.LanguageSelected -> {
                _homeUiState.update { it.copy(
                    selectedLanguage = event.language
                ) }

            }

            is UserEvent.ScrollToTop -> {
                viewModelScope.launch {
                    event.listState.scrollToItem(0)
                }
            }
        }

    }



}

data class HomeScreenInputs(
    val urlInput: String = "",
    val selectedLanguage: TranscriptLanguage = SupportedLanguages.first(),
)


sealed class TranscriptNetworkState {
    data object Loading : TranscriptNetworkState()
    data class Success(val transcript : TimeStampTranscript?) : TranscriptNetworkState()
    data class Error(val error : String) : TranscriptNetworkState()
}


sealed class UserEvent {
    data class UrlInputChanged(val url: String) : UserEvent()
    data class LanguageSelected(val language: TranscriptLanguage) : UserEvent()
    data object FetchTranscriptClicked : UserEvent()

    data class ScrollToTop(val listState: LazyListState) : UserEvent()

}