package com.monday8am.nqueenspuzzle.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monday8am.nqueenspuzzle.audio.SoundEffect
import com.monday8am.nqueenspuzzle.logic.NQueensGame
import com.monday8am.nqueenspuzzle.logic.models.Difficulty
import com.monday8am.nqueenspuzzle.logic.models.GameAction
import com.monday8am.nqueenspuzzle.logic.models.GameConfig
import com.monday8am.nqueenspuzzle.logic.models.Position
import com.monday8am.nqueenspuzzle.navigation.ResultsRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UserAction {
    data class TapCell(
        val position: Position,
    ) : UserAction()

    data class SetBoardSize(
        val size: Int,
    ) : UserAction()

    data class SetDifficulty(
        val difficulty: Difficulty,
    ) : UserAction()

    data object Reset : UserAction()
}


/**
 * ViewModel for the N-Queens game screen.
 * Acts as a thin adapter between the game engine and the UI.
 */
class GameViewModel(
    private val game: NQueensGame = NQueensGame(initialConfig = GameConfig()),
) : ViewModel() {

    private val _sideEffects = Channel<GameSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    val renderState: StateFlow<BoardRenderState> =
        game.state
            .onEach { state -> handleSideEffects(state) }
            .map { state -> buildBoardRenderState(state) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = createEmptyBoardRenderState(),
            )

    fun dispatch(action: UserAction) {
        when (action) {
            is UserAction.TapCell -> game.userMove(action.position)
            is UserAction.SetBoardSize -> game.restart(newConfig = game.config.copy(boardSize = action.size))
            is UserAction.SetDifficulty -> game.restart(newConfig = game.config.copy(difficulty = action.difficulty))
            is UserAction.Reset -> game.restart()
        }
    }

    private fun handleSideEffects(state: NQueensGame.NQueensState) {
        when (val action = state.lastAction) {
            is GameAction.QueenAdded,
            is GameAction.QueenMoved,
            is GameAction. QueenRemoved -> {
                if (action.causedConflict()) {
                    emitSideEffect(GameSideEffect.PlaySound(SoundEffect.QUEEN_CONFLICT))
                } else {
                    emitSideEffect(GameSideEffect.PlaySound(SoundEffect.QUEEN_PLACED))
                }
            }

            is GameAction.GameReset -> {
                emitSideEffect(GameSideEffect.PlaySound(SoundEffect.RESET_GAME))
            }

            is GameAction.GameWon -> {
                emitSideEffect(GameSideEffect.PlaySound(SoundEffect.GAME_WON))
                triggerWinNavigation(state.config.boardSize, state.elapsedTime)
            }

            else -> { /* No side effects for other actions */ }
        }
    }

    private fun buildBoardRenderState(state: NQueensGame.NQueensState): BoardRenderState {
        val startTime = System.currentTimeMillis()

        return BoardRenderState(
            boardSize = state.config.boardSize,
            difficulty = state.config.difficulty,
            queens = state.queens,
            selectedQueen = state.selectedQueen,
            queensRemaining = state.config.boardSize - state.queens.size,
            visibleConflicts = state.visibleConflicts,
            visibleAttackedCells = state.visibleAttackedCells,
            isSolved = state.isSolved,
            calculationTime = state.calculationTime + (System.currentTimeMillis() - startTime),
        )
    }

    private fun createEmptyBoardRenderState(): BoardRenderState =
        BoardRenderState(
            boardSize = game.config.boardSize,
            difficulty = Difficulty.EASY,
            queens = emptySet(),
            queensRemaining = 8,
            isSolved = false,
        )

    private fun triggerWinNavigation(
        boardSize: Int,
        gameTimeMillis: Long,
    ) {
        emitSideEffect(
            GameSideEffect.NavigateToResults(
                route =
                    ResultsRoute(
                        boardSize = boardSize,
                        elapsedSeconds = gameTimeMillis / 1000,
                    ),
            ),
        )
    }

    private fun emitSideEffect(effect: GameSideEffect) {
        viewModelScope.launch {
            _sideEffects.send(effect)
        }
    }
}
