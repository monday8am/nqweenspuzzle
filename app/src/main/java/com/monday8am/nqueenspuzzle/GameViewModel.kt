package com.monday8am.nqueenspuzzle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monday8am.nqueenspuzzle.logic.NQueensLogic
import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.Difficulty
import com.monday8am.nqueenspuzzle.models.Position
import com.monday8am.nqueenspuzzle.navigation.NavigationEvent
import com.monday8am.nqueenspuzzle.navigation.ResultsRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    data class GameState(
        val boardSize: Int = 8,
        val difficulty: Difficulty = Difficulty.EASY,
        val queens: Set<Position> = emptySet(),
        val selectedQueen: Position? = null,
        val gameStartTime: Long? = null,
        val gameEndTime: Long? = null,
    )

    private val _gameState = MutableStateFlow(GameState())

    val renderState: StateFlow<BoardRenderState> = _gameState
        .map { state ->
            NQueensLogic.buildBoardRenderState(
                boardSize = state.boardSize,
                queens = state.queens,
                selectedQueen = state.selectedQueen,
                difficulty = state.difficulty,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = NQueensLogic.buildBoardRenderState(
                boardSize = _gameState.value.boardSize,
                queens = _gameState.value.queens,
                selectedQueen = _gameState.value.selectedQueen,
                difficulty = _gameState.value.difficulty,
            )
        )

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun dispatch(action: GameAction) {
        _gameState.update { currentState ->
            val nextState = reduce(currentState, action)

            // Check for win condition and update gameEndTime in the same atomic update.
            val finalState = applyWinCondition(currentState, nextState)
            // Trigger navigation side effect if won.
            if (currentState.gameEndTime == null && finalState.gameEndTime != null) {
                triggerWinNavigation(finalState)
            }
            
            finalState
        }
    }

    private fun reduce(state: GameState, action: GameAction): GameState = when (action) {
        is GameAction.TapCell -> handleCellTap(state, action.position)
        is GameAction.SetBoardSize -> GameState(boardSize = action.size, difficulty = state.difficulty)
        is GameAction.SetDifficulty -> state.copy(difficulty = action.difficulty)
        is GameAction.Reset -> GameState(boardSize = state.boardSize, difficulty = state.difficulty)
    }

    private fun handleCellTap(state: GameState, position: Position): GameState {
        // Do not process taps if the game is already won.
        if (state.gameEndTime != null) return state

        val newQueens: Set<Position>
        val newSelected: Position?

        when {
            // Case 1: Tap on an existing queen to remove it.
            position in state.queens -> {
                newQueens = state.queens - position
                newSelected = if (position == state.selectedQueen) null else state.selectedQueen
            }
            // Case 2: Tap on an empty cell to move the selected queen.
            // Only if it's in conflict with another queen.
            state.selectedQueen != null && NQueensLogic.findConflictingQueens(state.queens).contains(state.selectedQueen) -> {
                newQueens = state.queens - state.selectedQueen + position
                newSelected = position
            }
            // Case 3: Tap on an empty cell to add a new queen (if board is not full).
            state.queens.size < state.boardSize -> {
                newQueens = state.queens + position
                newSelected = position
            }
            // Case 4: Board is full and no queen is selected -> do nothing.
            else -> return state
        }

        // Start the timer on the very first move.
        val startTime = if (state.queens.isEmpty() && newQueens.isNotEmpty()) {
            System.currentTimeMillis()
        } else {
            state.gameStartTime
        }

        return state.copy(
            queens = newQueens,
            selectedQueen = newSelected,
            gameStartTime = startTime
        )
    }

    private fun applyWinCondition(previousState: GameState, newState: GameState): GameState {
        // Only check if the game wasn't already won and is now solved.
        if (previousState.gameEndTime == null && NQueensLogic.isSolved(newState.queens, newState.boardSize)) {
            val endTime = System.currentTimeMillis()
            return newState.copy(gameEndTime = endTime)
        }
        return newState
    }

    private fun triggerWinNavigation(state: GameState) {
        val endTime = state.gameEndTime ?: return
        val startTime = state.gameStartTime ?: endTime
        val elapsedSeconds = (endTime - startTime) / 1000

        viewModelScope.launch {
            _navigationEvent.send(
                NavigationEvent.NavigateToResults(
                    route = ResultsRoute(
                        boardSize = state.boardSize,
                        elapsedSeconds = elapsedSeconds
                    )
                )
            )
        }
    }
}
