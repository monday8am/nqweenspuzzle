package com.monday8am.nqueenspuzzle.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monday8am.nqueenspuzzle.audio.SoundEffect
import com.monday8am.nqueenspuzzle.audio.SoundEffectManager
import com.monday8am.nqueenspuzzle.logic.NQueensGame
import com.monday8am.nqueenspuzzle.logic.models.Difficulty
import com.monday8am.nqueenspuzzle.logic.models.GameAction
import com.monday8am.nqueenspuzzle.logic.models.GameConfig
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
    private val soundEffectManager: SoundEffectManager? = null,
) : ViewModel() {
    val renderState: StateFlow<BoardRenderState> =
        game.state
            .onEach { state ->
                // Side effects: Play sounds based on game action
                when (val action = state.lastAction) {
                    is GameAction.QueenAdded -> {
                        if (action.causedConflict) {
                            soundEffectManager?.play(SoundEffect.QUEEN_CONFLICT)
                        } else {
                            soundEffectManager?.play(SoundEffect.QUEEN_PLACED)
                        }
                    }

                    is GameAction.QueenMoved -> {
                        if (action.causedConflict) {
                            soundEffectManager?.play(SoundEffect.QUEEN_CONFLICT)
                        } else {
                            soundEffectManager?.play(SoundEffect.QUEEN_PLACED)
                        }
                    }

                    is GameAction.GameWon -> {
                        soundEffectManager?.play(SoundEffect.GAME_WON)
                    }

                    is GameAction.QueenRemoved -> { /* No sound */
                    }

                    is GameAction.GameReset -> { /* No sound */
                    }

                    null -> { /* Initial state */
                    }
                }

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
