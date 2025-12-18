package com.monday8am.nqueenspuzzle.game

import com.monday8am.nqueenspuzzle.logic.NQueensLogic
import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.CellState
import com.monday8am.nqueenspuzzle.models.Difficulty
import com.monday8am.nqueenspuzzle.models.Position
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class NQueensGame(
    initialConfig: GameConfig = GameConfig(),
    private val logic: NQueensLogic = NQueensLogic,
) {

    val config: GameConfig
        get() = _gameState.value.config

    private data class InternalGameState(
        val config: GameConfig,
        val queens: Set<Position> = emptySet(),
        val selectedQueen: Position? = null,
        val gameStartTime: Long? = null,
        val gameEndTime: Long? = null,
    )

    private val _gameState = MutableStateFlow(InternalGameState(config = initialConfig))

    val state: Flow<GameState> = _gameState.map {
        val boardState = buildRenderState(it)
        if (it.gameEndTime == null) {
            GameState.Board(boardState)
        } else {
            GameState.UserWon(it.gameEndTime - it.gameStartTime!!, boardState)
        }
    }

    fun restart(newConfig: GameConfig? = null) {
        val config = newConfig ?: _gameState.value.config
        _gameState.update { InternalGameState(config = config) }
    }

    fun userMove(position: Position) {
        val current = _gameState.value
        if (current.gameEndTime != null) return

        _gameState.update {
            handleCellTap(current, position)
        }
    }

    private fun handleCellTap(state: InternalGameState, position: Position): InternalGameState {
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

        val endTime = if (logic.isSolved(newQueens, config.boardSize)) {
            System.currentTimeMillis()
        } else {
            state.gameEndTime
        }

        return state.copy(
            queens = newQueens,
            selectedQueen = newSelected,
            gameStartTime = startTime,
            gameEndTime = endTime
        )
    }

    private fun buildRenderState(state: InternalGameState): BoardRenderState {
        val startTime = System.currentTimeMillis()

        // Query game logic for conflict
        val allConflicts = logic.findConflictingQueens(state.queens)

        // Apply difficulty-based filtering (presentation logic)
        val visibleConflicts = when (state.config.difficulty) {
            Difficulty.EASY, Difficulty.MEDIUM -> allConflicts
            Difficulty.HARD -> {
                // Only show conflict on selected queen if it's conflicting
                if (state.selectedQueen != null && state.selectedQueen in allConflicts) {
                    setOf(state.selectedQueen)
                } else {
                    emptySet()
                }
            }
        }

        val visibleAttacks = when (state.config.difficulty) {
            Difficulty.EASY -> state.selectedQueen?.let {
                logic.getAttackedCells(it, state.config.boardSize)
            } ?: emptySet()
            Difficulty.MEDIUM, Difficulty.HARD -> emptySet()
        }

        // Build cell states
        val cells = buildList {
            for (row in 0 until state.config.boardSize) {
                for (col in 0 until state.config.boardSize) {
                    val position = Position(row, col)
                    val hasQueen = position in state.queens
                    val isConflicting = hasQueen && position in visibleConflicts
                    val isAttacked = position in visibleAttacks && !hasQueen
                    val isLightSquare = (row + col) % 2 == 0

                    add(
                        CellState(
                            position = position,
                            hasQueen = hasQueen,
                            isConflicting = isConflicting,
                            isAttacked = isAttacked,
                            isLightSquare = isLightSquare,
                            isSelected = position == state.selectedQueen,
                        )
                    )
                }
            }
        }

        return BoardRenderState(
            boardSize = state.config.boardSize,
            difficulty = state.config.difficulty,
            cells = cells,
            queensRemaining = state.config.boardSize - state.queens.size,
            isSolved = logic.isSolved(state.queens, state.config.boardSize),
            processingTime = System.currentTimeMillis() - startTime,
        )
    }
}
