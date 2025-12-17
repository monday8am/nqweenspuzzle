package com.monday8am.nqueenspuzzle

import androidx.lifecycle.ViewModel
import com.monday8am.nqueenspuzzle.logic.NQueensLogic
import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    data class GameState(
        val boardSize: Int = 8,
        val queens: Set<Position> = emptySet(),
        val selectedQueen: Position? = null,
        val showHints: Boolean = true,
        val showingHint: Boolean = false
    )

    private val _gameState = MutableStateFlow(GameState())

    private val _renderState = MutableStateFlow(buildRenderState(_gameState.value))
    val renderState: StateFlow<BoardRenderState> = _renderState.asStateFlow()

    fun dispatch(action: GameAction) {
        val newState = reduce(_gameState.value, action)
        _gameState.value = newState
        _renderState.value = buildRenderState(newState)
    }

    private fun reduce(state: GameState, action: GameAction): GameState {
        return when (action) {
            is GameAction.TapCell -> handleCellTap(state, action.position).copy(showingHint = false)
            is GameAction.SetBoardSize -> GameState(
                boardSize = action.size,
                showHints = state.showHints
            )
            is GameAction.Reset -> GameState(
                boardSize = state.boardSize,
                showHints = state.showHints
            )
            is GameAction.ToggleHint -> state.copy(showingHint = !state.showingHint)
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
                state.copy(
                    queens = state.queens + position,
                    selectedQueen = position
                )
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
        val hintPosition = if (state.showingHint) {
            NQueensLogic.getHint(state.queens, state.boardSize)
        } else null

        return NQueensLogic.buildBoardRenderState(
            boardSize = state.boardSize,
            queens = state.queens,
            selectedQueen = selectedForHints,
            hintPosition = hintPosition
        )
    }
}
