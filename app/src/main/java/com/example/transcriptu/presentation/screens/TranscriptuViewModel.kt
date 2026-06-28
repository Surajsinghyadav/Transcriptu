package com.example.transcriptu.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcriptu.Koin.buildTranscriptuService
import com.example.transcriptu.TranscriptuRepository
import com.example.transcriptu.data.modal.TimeStampTranscript
import com.transcriptapp.ui.components.SupportedLanguages
import com.transcriptapp.ui.components.TranscriptLanguage
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import retrofit2.HttpException

fun Double.toYouTubeTimestamp(): String {
    val totalSeconds = this.toLong()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

fun String.offsetToTimestamp(): String {
    val seconds = this.toDoubleOrNull() ?: return this
    return seconds.toYouTubeTimestamp()
}

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "Api_Key")
class TranscriptuViewModel(
     context: Context,
    private val repository: TranscriptuRepository,
) : ViewModel() {


    companion object {
        val API_KEY_PREFERENCE_KEY = stringPreferencesKey("Api_Key")
    }

    private val _networkUiState = MutableStateFlow<TranscriptNetworkState>(TranscriptNetworkState.Success(null))
    val networkUiState = _networkUiState.asStateFlow()

    private val _homeUiState = MutableStateFlow(HomeScreenInputs())
    val homeUiState = _homeUiState.asStateFlow()

    private val _transcriptDetailUiState = MutableStateFlow(TranscriptDetailUiState())
    val transcriptDetailUiState = _transcriptDetailUiState.asStateFlow()

    private val _savedApiKey = MutableStateFlow("")

    val savedApiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[API_KEY_PREFERENCE_KEY]
    }

    private val _apiKey = savedApiKey.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    private val apiKey = _apiKey



    val _copySuccess = MutableStateFlow(false)
    val copySuccess = _copySuccess.asStateFlow()



    fun saveApiKey(context: Context, key: String) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[API_KEY_PREFERENCE_KEY] = key.trim()
            }
        }

    }

    fun copyTranscript(context: Context, withTimestamps: Boolean) {
        val segments = _transcriptDetailUiState.value.transcriptSegment
        val isPlain = _transcriptDetailUiState.value.isTranscriptNotAvailable

        val textToCopy = when {
            isPlain -> _transcriptDetailUiState.value.description
            withTimestamps -> segments.joinToString("\n") { segment ->
                "[${segment.offset.offsetToTimestamp()}] ${segment.text.orEmpty()}"
            }
            else -> segments.joinToString(" ") { it.text.orEmpty() }
        }

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Transcript2", textToCopy)
        clipboard.setPrimaryClip(clip)

        viewModelScope.launch {
            _copySuccess.value = true
            kotlinx.coroutines.delay(2000)
            _copySuccess.value = false
        }
    }

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.UrlInputChanged -> {
                _homeUiState.update { it.copy(urlInput = event.url) }
            }

            UserEvent.FetchTranscriptClicked -> {
                viewModelScope.launch {
                    val apiKey = apiKey.value
                    Log.e("ApiKey", "$apiKey")
                    if (apiKey.isNullOrBlank()) {
                        return@launch
                    }

                    _networkUiState.update { TranscriptNetworkState.Loading }
                    _transcriptDetailUiState.update { TranscriptDetailUiState() }

                    val url = _homeUiState.value.urlInput
                    val language = _homeUiState.value.selectedLanguage
                    val dynamicRepository = TranscriptuRepository(
                        buildTranscriptuService(apiKey),
                        repository.metaDataFetcher
                    )

                    supervisorScope {
                        val metadataAsync = async { dynamicRepository.getMetadata(url) }
                        val transcriptAsync = async { dynamicRepository.getTimestampTranscript(url, language) }

                        try {
                            val metaData = metadataAsync.await()
                            _transcriptDetailUiState.update {
                                it.copy(
                                    videoTitle = metaData.title ?: "Untitled",
                                    description = metaData.description ?: "",
                                    thumbnailUrl = metaData.thumbnailUrl,
                                    videoUrl = metaData.videoUrl ?: ""
                                )
                            }
                            Log.d("description", metaData.description ?: "its empty chit")
                            _networkUiState.update { TranscriptNetworkState.Success(null) }
                        } catch (e: Exception) {
                            _networkUiState.update { TranscriptNetworkState.Error(resolveError(e)) }
                        }

                        try {
                            val transcript = transcriptAsync.await()
                            if (transcript.success && transcript.transcript != null) {
                                _transcriptDetailUiState.update { state ->
                                    val detectedLang = transcript.transcript.firstOrNull()?.lang
                                    state.copy(
                                        transcriptSegment = transcript.transcript,
                                        language = SupportedLanguages.find { it.code == detectedLang }
                                            ?: SupportedLanguages.first(),
                                        isLoading = false
                                    )
                                }
                            } else {
                                _transcriptDetailUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        isTranscriptNotAvailable = true,
                                        apiErrorMessage = transcript.error ?: "Transcript not available for this video"
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            val errMsg = resolveError(e)
                            _transcriptDetailUiState.update {
                                it.copy(
                                    isLoading = false,
                                    isTranscriptNotAvailable = true,
                                    apiErrorMessage = errMsg
                                )
                            }
                            _networkUiState.update { TranscriptNetworkState.Error(errMsg) }
                        }
                    }
                }
            }

            is UserEvent.LanguageSelected -> {
                _homeUiState.update { it.copy(selectedLanguage = event.language) }
            }

            is UserEvent.ScrollToTop -> {
                viewModelScope.launch {
                    event.listState.scrollToItem(0)
                }
            }
        }
    }

    private fun resolveError(e: Exception): String {
        return if (e is HttpException) {
            when (e.code()) {
                429 -> "Rate limit exceeded (HTTP 429). You've used all 100 free requests this month. Upgrade your RapidAPI plan or wait until the 1st of next month."
                401 -> "Invalid API key (HTTP 401). Please check your key in Settings."
                403 -> "Access forbidden (HTTP 403). Your API key may not have access to this API."
                404 -> "Video not found (HTTP 404). Please check the YouTube URL."
                500, 502, 503 -> "Server error (HTTP ${e.code()}). Please try again later."
                else -> "Network error (HTTP ${e.code()}): ${e.message()}"
            }
        } else {
            e.localizedMessage ?: "An unexpected error occurred"
        }
    }
}

data class HomeScreenInputs(
    val urlInput: String = "",
    val selectedLanguage: TranscriptLanguage = SupportedLanguages.first(),
)

sealed class TranscriptNetworkState {
    data object Loading : TranscriptNetworkState()
    data class Success(val transcript: TimeStampTranscript?) : TranscriptNetworkState()
    data class Error(val error: String) : TranscriptNetworkState()
}

sealed class UserEvent {
    data class UrlInputChanged(val url: String) : UserEvent()
    data class LanguageSelected(val language: TranscriptLanguage) : UserEvent()
    data object FetchTranscriptClicked : UserEvent()
    data class ScrollToTop(val listState: LazyListState) : UserEvent()
}