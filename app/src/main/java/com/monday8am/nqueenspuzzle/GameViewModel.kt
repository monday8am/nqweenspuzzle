package com.monday8am.nqueenspuzzle

import androidx.lifecycle.ViewModel
import com.monday8am.nqueenspuzzle.logic.NQueensLogic
import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private data class GameState(
        val boardSize: Int = 8,
        val queens: Set<Position> = emptySet(),
        val selectedQueen: Position? = null,
        val elapsedTimeMs: Long = 0
    )

    private val _gameState = MutableStateFlow(GameState())

    private val _renderState = MutableStateFlow(buildRenderState(_gameState.value))
    val renderState: StateFlow<BoardRenderState> = _renderState.asStateFlow()

    fun onCellTap(position: Position) {
        val currentState = _gameState.value

        val newState = when {
            // Tapping on empty cell: place a queen
            position !in currentState.queens -> {
                currentState.copy(
                    queens = currentState.queens + position,
                    selectedQueen = null
                )
            }
            // Tapping on selected queen: remove it
            position == currentState.selectedQueen -> {
                currentState.copy(
                    queens = currentState.queens - position,
                    selectedQueen = null
                )
            }
            // Tapping on unselected queen: select it
            else -> {
                currentState.copy(selectedQueen = position)
            }
        }

        _gameState.value = newState
        _renderState.value = buildRenderState(newState)
    }

    fun reset() {
        val currentState = _gameState.value
        val newState = GameState(boardSize = currentState.boardSize)
        _gameState.value = newState
        _renderState.value = buildRenderState(newState)
    }

    fun setBoardSize(size: Int) {
        val newState = GameState(boardSize = size)
        _gameState.value = newState
        _renderState.value = buildRenderState(newState)
    }

    private fun buildRenderState(state: GameState): BoardRenderState {
        return NQueensLogic.buildBoardRenderState(
            boardSize = state.boardSize,
            queens = state.queens,
            selectedQueen = state.selectedQueen,
            elapsedTimeMs = state.elapsedTimeMs
        )
    }
}
