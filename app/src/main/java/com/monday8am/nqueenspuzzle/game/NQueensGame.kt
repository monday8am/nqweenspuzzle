package com.monday8am.nqueenspuzzle.game

import com.monday8am.nqueenspuzzle.logic.NQueensLogic
import com.monday8am.nqueenspuzzle.models.Difficulty
import com.monday8am.nqueenspuzzle.models.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NQueensGame(
    initialConfig: GameConfig = GameConfig(),
    private val logic: NQueensLogic = NQueensLogic,
) {

    val config: GameConfig
        get() = _state.value.config

    data class NQueensState(
        val config: GameConfig,
        val queens: Set<Position> = emptySet(),
        val selectedQueen: Position? = null,
        val gameStartTime: Long? = null,
        val gameEndTime: Long? = null,
        val visibleConflicts: Set<Position> = emptySet(),
        val visibleAttackedCells: Set<Position> = emptySet(),
        val calculationTime: Long = 0,
    ) {
        val isSolved: Boolean
            get() = gameStartTime != null && gameEndTime != null

        val elapsedTime: Long
            get() = if (isSolved) (gameEndTime!!) - (gameStartTime!!) else 0L
    }

    private val _state = MutableStateFlow(NQueensState(config = initialConfig))
    val state: StateFlow<NQueensState> = _state

    fun restart(newConfig: GameConfig? = null) {
        val config = newConfig ?: _state.value.config
        _state.update { NQueensState(config = config) }
    }

    fun userMove(position: Position) {
        val current = _state.value
        if (current.gameEndTime != null) return

        _state.update {
            val startTime = System.currentTimeMillis()
            handleCellTap(current, position)
                .copy(calculationTime = System.currentTimeMillis() - startTime)
        }
    }

    private fun handleCellTap(state: NQueensState, position: Position): NQueensState {
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
            state.selectedQueen != null && logic.findConflictingQueens(state.queens).contains(state.selectedQueen) -> {
                newQueens = state.queens - state.selectedQueen + position
                newSelected = position
            }
            // Case 3: Tap on an empty cell to add a new queen (if board is not full).
            state.queens.size < state.config.boardSize -> {
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

        val isSolved = logic.isSolved(newQueens, config.boardSize)
        val endTime = if (isSolved) {
            System.currentTimeMillis()
        } else {
            state.gameEndTime
        }

        // Calculate game-logic based visibility
        val allConflicts = logic.findConflictingQueens(newQueens)
        val visibleConflicts = when (state.config.difficulty) {
            Difficulty.EASY, Difficulty.MEDIUM -> allConflicts
            Difficulty.HARD -> {
                // Only show conflict on selected queen if it's conflicting
                if (newSelected != null && newSelected in allConflicts) {
                    setOf(newSelected)
                } else {
                    emptySet()
                }
            }
        }

        val visibleAttacks = when (state.config.difficulty) {
            Difficulty.EASY -> newSelected?.let {
                logic.getAttackedCells(it, state.config.boardSize)
            } ?: emptySet()

            Difficulty.MEDIUM, Difficulty.HARD -> emptySet()
        }

        return state.copy(
            queens = newQueens,
            selectedQueen = newSelected,
            gameStartTime = startTime,
            gameEndTime = endTime,
            visibleConflicts = visibleConflicts,
            visibleAttackedCells = visibleAttacks,
        )
    }
}
