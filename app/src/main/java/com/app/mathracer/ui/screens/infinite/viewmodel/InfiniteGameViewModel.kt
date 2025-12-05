package com.app.mathracer.ui.screens.infinite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.network.ApiService
import com.app.mathracer.data.network.InfiniteQuestionDto
import com.app.mathracer.data.network.InfiniteAnswerRequest
import com.app.mathracer.data.network.RetrofitClient
import com.app.mathracer.data.repository.GarageRepository
import com.app.mathracer.data.repository.UserRemoteRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class InfiniteUiState(
    val gameId: Int? = null,
    val playerName: String = "",
    val playerCarProductId: Int? = null,
    val playerTrackProductId: Int? = null,
    val expression: String = "",
    val options: List<Int?> = listOf(null, null, null, null),
    val expectedResult: String = "",
    val lastAnswerGiven: Int? = null,
    val lastAnswerWasCorrect: Boolean? = null,
    val showAnswerFeedback: Boolean = false,
    val isWaitingForAnswer: Boolean = false,
    val isPenalized: Boolean = false,
    val yourProgress: Int = 0,
    val currentBatch: Int = 0,
    val currentQuestionIndex: Int = 0,
    val totalAnswered: Int = 0
)

data class InfiniteAbandonResult(
    val gameId: Int,
    val playerName: String,
    val totalCorrectAnswers: Int,
    val currentQuestionIndex: Int,
    val currentBatch: Int,
    val gameStartedAt: String?,
    val abandonedAt: String?,
    val timePlayed: String
)

class InfiniteGameViewModel : ViewModel() {
    private val api: ApiService = RetrofitClient.api

    private val _state = MutableStateFlow(InfiniteUiState())
    val state: StateFlow<InfiniteUiState> = _state

    private val _abandonResult = MutableStateFlow<InfiniteAbandonResult?>(null)
    val abandonResult: StateFlow<InfiniteAbandonResult?> = _abandonResult

    private val questions = ArrayList<InfiniteQuestionDto>()

    fun start(gameIdString: String) {
        viewModelScope.launch {
            if (gameIdString == "local") {
                startNewInfiniteGame()
            } else {
                val gid = gameIdString.toIntOrNull()
                if (gid != null) {
                    loadStatus(gid)
                } else {
                    startNewInfiniteGame()
                }
            }
        }
    }

    private suspend fun startNewInfiniteGame() {
        try {
            val token = try { UserRemoteRepository.getIdToken() } catch (e: Exception) { null }
            val header = token?.let { "Bearer $it" }
            val resp = api.startInfinite(header)
            if (resp.isSuccessful) {
                resp.body()?.let { body ->
                    questions.clear()
                    questions.addAll(body.questions)
                    applyQuestionFromQueue()
                    _state.value = _state.value.copy(
                        gameId = body.gameId,
                        playerName = body.playerName,
                        yourProgress = 0,
                        currentBatch = body.currentBatch,
                        currentQuestionIndex = 0,
                        totalAnswered = (body.currentBatch * 9) + 0
                    )
                    try {
                        val repo = GarageRepository()
                        val playerId = CurrentUser.user?.id
                        if (playerId != null) {
                            coroutineScope {
                                val carDeferred = async { repo.getCars(playerId) }
                                val bgDeferred = async { repo.getBackgrounds(playerId) }
                                val carId = carDeferred.await().getOrNull()?.activeItem?.productId
                                val bgId = bgDeferred.await().getOrNull()?.activeItem?.productId
                                _state.value = _state.value.copy(playerCarProductId = carId, playerTrackProductId = bgId)
                            }
                        } else {

                        }
                    } catch (e: Exception) {
                        android.util.Log.w("InfiniteVM", "Could not fetch player products: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("InfiniteVM", "startNewInfiniteGame error", e)
        }
    }

    private suspend fun loadStatus(gid: Int) {
        try {
            val token = try { UserRemoteRepository.getIdToken() } catch (e: Exception) { null }
            val header = token?.let { "Bearer $it" }
            val resp = api.getInfiniteStatus(header, gid)
            if (resp.isSuccessful) {
                resp.body()?.let { body ->
                    _state.value = _state.value.copy(
                        gameId = body.gameId,
                        playerName = body.playerName,
                        yourProgress = body.totalCorrectAnswers,
                        currentBatch = body.currentBatch,
                        currentQuestionIndex = body.currentQuestionIndex,
                        totalAnswered = (body.currentBatch * 9) + body.currentQuestionIndex + 1
                    )
                    loadBatch(gid)
                    try {
                        val repo = GarageRepository()
                        val playerId = CurrentUser.user?.id
                        if (playerId != null) {
                            coroutineScope {
                                val carDeferred = async { repo.getCars(playerId) }
                                val bgDeferred = async { repo.getBackgrounds(playerId) }
                                val carId = carDeferred.await().getOrNull()?.activeItem?.productId
                                val bgId = bgDeferred.await().getOrNull()?.activeItem?.productId
                                _state.value = _state.value.copy(playerCarProductId = carId, playerTrackProductId = bgId)
                            }
                        } else {

                        }
                    } catch (e: Exception) {
                        android.util.Log.w("InfiniteVM", "Could not fetch player products: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("InfiniteVM", "loadStatus error", e)
        }
    }

    private suspend fun loadBatch(gid: Int) {
        try {
            val token = try { UserRemoteRepository.getIdToken() } catch (e: Exception) { null }
            val header = token?.let { "Bearer $it" }
            val resp = api.loadInfiniteBatch(header, gid)
            if (resp.isSuccessful) {
                resp.body()?.let { body ->
                    questions.clear()
                    questions.addAll(body.questions)
                    _state.value = _state.value.copy(
                        yourProgress = body.totalCorrectAnswers,
                        currentBatch = body.currentBatch,
                        currentQuestionIndex = 0,
                        totalAnswered = (body.currentBatch * 9) + 0
                    )
                    applyQuestionFromQueue()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("InfiniteVM", "loadBatch error", e)
        }
    }

    private fun applyQuestionFromQueue() {
        val q = questions.firstOrNull()
        if (q != null) {
            _state.value = _state.value.copy(
                expression = q.equation,
                options = listOf(
                    q.options.getOrNull(0),
                    q.options.getOrNull(1),
                    q.options.getOrNull(2),
                    q.options.getOrNull(3)
                ),
                expectedResult = q.expectedResult ?: "",
                showAnswerFeedback = false,
                lastAnswerGiven = null,
                lastAnswerWasCorrect = null,
                isWaitingForAnswer = false
            )
        }
    }

    fun submitAnswer(optionIndex: Int) {
        viewModelScope.launch {
            val gid = _state.value.gameId ?: return@launch
            val q = questions.firstOrNull() ?: return@launch
            val selected = q.options.getOrNull(optionIndex) ?: return@launch

            _state.value = _state.value.copy(isWaitingForAnswer = true, lastAnswerGiven = selected)

            try {
                val token = try { UserRemoteRepository.getIdToken() } catch (e: Exception) { null }
                val header = token?.let { "Bearer $it" }
                val resp = api.submitInfiniteAnswer(header, gid, InfiniteAnswerRequest(selected))
                if (resp.isSuccessful) {
                    resp.body()?.let { body ->
                        val newBatch = _state.value.currentBatch
                        val newIndex = body.currentQuestionIndex
                        val computedTotal = (newBatch * 9) + newIndex + 1

                        _state.value = _state.value.copy(
                            lastAnswerWasCorrect = body.isCorrect,
                            showAnswerFeedback = true,
                            yourProgress = body.totalCorrectAnswers,
                            currentQuestionIndex = newIndex,
                            totalAnswered = computedTotal,
                            isWaitingForAnswer = false
                        )

                         
                        if (questions.isNotEmpty()) questions.removeAt(0)

                        if (body.needsNewBatch || questions.isEmpty()) {
                            loadBatch(gid)
                        } else {
                            applyQuestionFromQueue()
                        }
                    }
                } else {
                    _state.value = _state.value.copy(isWaitingForAnswer = false)
                }
            } catch (e: Exception) {
                android.util.Log.e("InfiniteVM", "submitAnswer error", e)
                _state.value = _state.value.copy(isWaitingForAnswer = false)
            }
        }
    }

    fun abandonGame() {
        viewModelScope.launch {
            val gid = _state.value.gameId ?: return@launch
            try {
                val token = try { UserRemoteRepository.getIdToken() } catch (e: Exception) { null }
                val header = token?.let { "Bearer $it" }
                val resp = api.abandonInfinite(header, gid)
                if (resp.isSuccessful) {
                    resp.body()?.let { body ->
                        
                        val played = computeTimePlayed(body.gameStartedAt, body.abandonedAt)
                        val result = InfiniteAbandonResult(
                            gameId = body.gameId,
                            playerName = body.playerName,
                            totalCorrectAnswers = body.totalCorrectAnswers,
                            currentQuestionIndex = body.currentQuestionIndex,
                            currentBatch = body.currentBatch,
                            gameStartedAt = body.gameStartedAt,
                            abandonedAt = body.abandonedAt,
                            timePlayed = played
                        )
                        _abandonResult.value = result
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("InfiniteVM", "abandonGame error", e)
            }
        }
    }

    private fun computeTimePlayed(startIso: String?, endIso: String?): String {
        if (startIso == null || endIso == null) return "--:--"
        return try {
            val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            fmt.timeZone = TimeZone.getTimeZone("UTC")
            val start = fmt.parse(startIso)
            val end = fmt.parse(endIso)
            if (start == null || end == null) return "--:--"
            val diff = end.time - start.time
            val seconds = (diff / 1000) % 60
            val minutes = (diff / (1000 * 60)) % 60
            val hours = diff / (1000 * 60 * 60)
            if (hours > 0) String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
            else String.format(Locale.US, "%02d:%02d", minutes, seconds)
        } catch (e: Exception) {
            android.util.Log.e("InfiniteVM", "computeTimePlayed error", e)
            "--:--"
        }
    }
}
