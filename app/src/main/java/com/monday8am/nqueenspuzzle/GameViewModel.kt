package com.monday8am.nqueenspuzzle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monday8am.nqueenspuzzle.logic.NQueensLogic
import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.Position
import com.monday8am.nqueenspuzzle.navigation.NavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    data class GameState(
        val boardSize: Int = 8,
        val queens: Set<Position> = emptySet(),
        val selectedQueen: Position? = null,
        val showHints: Boolean = true,
        val gameStartTime: Long? = null,
        val gameEndTime: Long? = null
    )

    private val _gameState = MutableStateFlow(GameState())

    private val _renderState = MutableStateFlow(buildRenderState(_gameState.value))
    val renderState: StateFlow<BoardRenderState> = _renderState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    fun dispatch(action: GameAction) {
        val newState = reduce(_gameState.value, action)
        _gameState.value = newState
        _renderState.value = buildRenderState(newState)
    }

    private fun reduce(state: GameState, action: GameAction): GameState {
        return when (action) {
            is GameAction.TapCell -> handleCellTap(state, action.position)
            is GameAction.SetBoardSize -> GameState(
                boardSize = action.size,
                showHints = state.showHints
            )
            is GameAction.Reset -> GameState(
                boardSize = state.boardSize,
                showHints = state.showHints
            )
        }
    }

    private fun handleCellTap(state: GameState, position: Position): GameState {
        return when {
            position in state.queens -> {
                state.copy(
                    queens = state.queens - position,
                    selectedQueen = null
                )
            }

            state.queens.size < state.boardSize -> {
                val newState = state.copy(
                    queens = state.queens + position,
                    selectedQueen = position
                )
                // Start timer on first queen placement
                if (state.queens.isEmpty() && state.gameStartTime == null) {
                    newState.copy(gameStartTime = System.currentTimeMillis())
                } else {
                    newState
                }
            }

            state.selectedQueen != null -> {
                state.copy(
                    queens = state.queens - state.selectedQueen + position,
                    selectedQueen = position
                )
            }

            // Improbable case: Tapped on an empty cell, board is full, and no queen is selected.
            else -> state // No change
        }
    }


    private fun buildRenderState(state: GameState): BoardRenderState {
        val selectedForHints = if (state.showHints) state.selectedQueen else null
        val renderState = NQueensLogic.buildBoardRenderState(
            boardSize = state.boardSize,
            queens = state.queens,
            selectedQueen = selectedForHints
        )

        // Capture end time and emit navigation event when puzzle is solved
        if (renderState.isSolved && state.gameEndTime == null) {
            val updatedState = state.copy(gameEndTime = System.currentTimeMillis())
            _gameState.value = updatedState

            val elapsedSeconds = getElapsedTimeSeconds(updatedState)
            if (elapsedSeconds != null) {
                viewModelScope.launch {
                    _navigationEvent.emit(
                        NavigationEvent.NavigateToResults(
                            boardSize = state.boardSize,
                            elapsedSeconds = elapsedSeconds
                        )
                    )
                }
            }
        }

        return renderState
    }

    private fun getElapsedTimeSeconds(state: GameState): Long? {
        val start = state.gameStartTime ?: return null
        val end = state.gameEndTime ?: return null
        return (end - start) / 1000  // Convert ms to seconds
    }
}
