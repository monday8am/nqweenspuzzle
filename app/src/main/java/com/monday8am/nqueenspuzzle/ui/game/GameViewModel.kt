package com.monday8am.nqueenspuzzle.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monday8am.nqueenspuzzle.ui.game.UserAction
import com.monday8am.nqueenspuzzle.logic.models.GameConfig
import com.monday8am.nqueenspuzzle.logic.NQueensGame
import com.monday8am.nqueenspuzzle.logic.models.Difficulty
import com.monday8am.nqueenspuzzle.logic.models.Position
import com.monday8am.nqueenspuzzle.navigation.NavigationEvent
import com.monday8am.nqueenspuzzle.navigation.ResultsRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the N-Queens game screen.
 * Acts as a thin adapter between the game engine and the UI.
 */
class GameViewModel(
    private val game: NQueensGame = NQueensGame(initialConfig = GameConfig()),
) : ViewModel() {
    val renderState: StateFlow<BoardRenderState> =
        game.state
            .onEach { state ->
                // Side effects: Check for win condition
                if (state.isSolved) {
                    triggerWinNavigation(state.config.boardSize, state.elapsedTime)
                }
            }.map { state -> buildBoardRenderState(state) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = createEmptyBoardRenderState(),
            )

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun dispatch(action: UserAction) {
        when (action) {
            is UserAction.TapCell -> game.userMove(action.position)
            is UserAction.SetBoardSize -> game.restart(newConfig = game.config.copy(boardSize = action.size))
            is UserAction.SetDifficulty -> game.restart(newConfig = game.config.copy(difficulty = action.difficulty))
            is UserAction.Reset -> game.restart()
        }
    }

    private fun buildBoardRenderState(state: NQueensGame.NQueensState): BoardRenderState {
        val cells =
            buildList {
                for (row in 0 until state.config.boardSize) {
                    for (col in 0 until state.config.boardSize) {
                        val position = Position(row, col)
                        val hasQueen = position in state.queens
                        val isConflicting = hasQueen && position in state.visibleConflicts
                        val isAttacked = position in state.visibleAttackedCells && !hasQueen
                        val isLightSquare = (row + col) % 2 == 0

                        add(
                            CellState(
                                position = position,
                                hasQueen = hasQueen,
                                isConflicting = isConflicting,
                                isAttacked = isAttacked,
                                isLightSquare = isLightSquare,
                                isSelected = position == state.selectedQueen,
                            ),
                        )
                    }
                }
            }

        return BoardRenderState(
            boardSize = state.config.boardSize,
            difficulty = state.config.difficulty,
            cells = cells,
            queensRemaining = state.config.boardSize - state.queens.size,
            isSolved = state.isSolved,
            calculationTime = state.calculationTime,
        )
    }

    private fun createEmptyBoardRenderState(): BoardRenderState =
        BoardRenderState(
            boardSize = game.config.boardSize,
            difficulty = Difficulty.EASY,
            cells = emptyList(),
            queensRemaining = 8,
            isSolved = false,
        )

    private fun triggerWinNavigation(boardSize: Int, gameTimeMillis: Long) {
        viewModelScope.launch {
            _navigationEvent.send(
                NavigationEvent.NavigateToResults(
                    route =
                        ResultsRoute(
                            boardSize = boardSize,
                            elapsedSeconds = gameTimeMillis / 1000,
                        ),
                ),
            )
        }
    }
}
