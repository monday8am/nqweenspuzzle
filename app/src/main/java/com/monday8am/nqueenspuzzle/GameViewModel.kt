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
        val showHint: Boolean = false,
        val gameStartTime: Long? = null,
        val gameEndTime: Long? = null,
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

    private fun reduce(
        state: GameState,
        action: GameAction,
    ): GameState =
        when (action) {
            is GameAction.TapCell -> {
                handleCellTap(state, action.position).copy(showHint = false)
            }

            is GameAction.SetBoardSize -> {
                GameState(
                    boardSize = action.size,
                    showHint = false,
                )
            }

            is GameAction.Reset -> {
                GameState(
                    boardSize = state.boardSize,
                    showHint = false,
                )
            }

            is GameAction.ShowHint -> {
                state.copy(showHint = true)
            }
        }

    private fun handleCellTap(
        state: GameState,
        position: Position,
    ): GameState =
        when {
            // remove queen
            position in state.queens -> {
                state.copy(
                    queens = state.queens - position,
                    selectedQueen = null,
                )
            }

            // add queen
            state.queens.size <= state.boardSize -> {
                val newState =
                    if (state.selectedQueen != null && NQueensLogic.findConflictingQueens(state.queens).isNotEmpty()) {
                        state.copy(
                            queens = state.queens - state.selectedQueen + position,
                            selectedQueen = position,
                        )
                    } else {
                        state.copy(
                            queens = state.queens + position,
                            selectedQueen = position,
                        )
                    }
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
                    selectedQueen = position,
                )
            }

            // Improbable case: Tapped on an empty cell, board is full, and no queen is selected.
            else -> {
                state
            } // No change
        }

    private fun buildRenderState(state: GameState): BoardRenderState {
        val renderState =
            NQueensLogic.buildBoardRenderState(
                boardSize = state.boardSize,
                queens = state.queens,
                selectedQueen = state.selectedQueen,
                showHint = state.showHint,
            )

        // Capture end time and emit navigation event when puzzle is solved
        // TODO: deal with this side effect
        if (renderState.isSolved && state.gameEndTime == null) {
            val updatedState = state.copy(gameEndTime = System.currentTimeMillis())
            _gameState.value = updatedState

            val elapsedSeconds = getElapsedTimeSeconds(updatedState)
            if (elapsedSeconds != null) {
                viewModelScope.launch {
                    _navigationEvent.emit(
                        NavigationEvent.NavigateToResults(
                            route =
                                com.monday8am.nqueenspuzzle.navigation.ResultsRoute(
                                    boardSize = state.boardSize,
                                    elapsedSeconds = elapsedSeconds,
                                ),
                        ),
                    )
                }
            }
        }

        return renderState
    }

    private fun getElapsedTimeSeconds(state: GameState): Long? {
        val start = state.gameStartTime ?: return null
        val end = state.gameEndTime ?: return null
        return (end - start) / 1000 // Convert ms to seconds
    }
}
