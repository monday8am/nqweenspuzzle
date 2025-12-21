package com.monday8am.nqueenspuzzle.ui.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.monday8am.nqueenspuzzle.data.ScoreEntry
import com.monday8am.nqueenspuzzle.data.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultsViewModel(
    private val scoreRepository: ScoreRepository,
    val boardSize: Int,
    val elapsedSeconds: Long,
) : ViewModel() {
    private val _scores = MutableStateFlow<List<ScoreEntry>>(emptyList())
    val scores: StateFlow<List<ScoreEntry>> = _scores.asStateFlow()

    init {
        saveScore()
        loadScores()
    }

    private fun saveScore() {
        viewModelScope.launch {
            scoreRepository.addScore(boardSize, elapsedSeconds)
        }
    }

    private fun loadScores() {
        viewModelScope.launch {
            scoreRepository.getScoresForBoardSize(boardSize).collect { scoreList ->
                _scores.value = scoreList
            }
        }
    }

    companion object {
        fun provideFactory(
            scoreRepository: ScoreRepository,
            boardSize: Int,
            elapsedSeconds: Long,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ResultsViewModel(
                        scoreRepository = scoreRepository,
                        boardSize = boardSize,
                        elapsedSeconds = elapsedSeconds,
                    ) as T
                }
            }
    }
}
