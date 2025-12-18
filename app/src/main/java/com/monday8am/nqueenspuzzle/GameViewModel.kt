package com.monday8am.nqueenspuzzle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monday8am.nqueenspuzzle.game.GameConfig
import com.monday8am.nqueenspuzzle.game.GameState
import com.monday8am.nqueenspuzzle.game.NQueensGame
import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.Difficulty
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
class GameViewModel : ViewModel() {
    // Game engine instance
    private val game = NQueensGame(initialConfig = GameConfig())

    val renderState: StateFlow<BoardRenderState> = game.state
        .onEach { state -> if(state is GameState.UserWon) triggerWinNavigation(state.gameTimeMillis) }
        .map { state ->
            when (state) {
                is GameState.Board -> state.renderState
                is GameState.UserWon -> state.renderState
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = createEmptyBoardRenderState(),
        )

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun dispatch(action: GameAction) {
        when (action) {
            is GameAction.TapCell -> game.userMove(action.position)
            is GameAction.SetBoardSize -> game.restart(game.config.copy(boardSize = action.size))
            is GameAction.SetDifficulty -> game.restart(game.config.copy(difficulty = action.difficulty))
            is GameAction.Reset -> game.restart()
        }
    }

    private fun createEmptyBoardRenderState(): BoardRenderState {
        return BoardRenderState(
            boardSize = 8,
            difficulty = Difficulty.EASY,
            cells = emptyList(),
            queensRemaining = 8,
            isSolved = false,
            processingTime = 0L,
        )
    }

    private fun triggerWinNavigation(gameTimeMillis: Long) {
        val elapsedSeconds = gameTimeMillis / 1000

        viewModelScope.launch {
            _navigationEvent.send(
                NavigationEvent.NavigateToResults(
                    route = ResultsRoute(
                        boardSize = game.config.boardSize,
                        elapsedSeconds = elapsedSeconds
                    )
                )
            )
        }
    }
}
