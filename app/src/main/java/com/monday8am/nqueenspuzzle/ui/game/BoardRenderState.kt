package com.monday8am.nqueenspuzzle.ui.game

import androidx.annotation.VisibleForTesting
import com.monday8am.nqueenspuzzle.logic.models.Difficulty
import com.monday8am.nqueenspuzzle.logic.models.Position

data class BoardRenderState(
    val boardSize: Int,
    val difficulty: Difficulty,
    val queens: Set<Position> = emptySet(),
    val selectedQueen: Position? = null,
    val queensRemaining: Int,
    val isSolved: Boolean,
    val visibleConflicts: Set<Position> = emptySet(),
    val visibleAttackedCells: Set<Position> = emptySet(),
    @get:VisibleForTesting
    val calculationTime: Long = 0L,
) {
    fun isQueen(position: Position): Boolean = position in queens

    fun isConflicting(position: Position): Boolean = position in visibleConflicts

    fun isAttacked(position: Position): Boolean = position in visibleAttackedCells

    fun isSelected(position: Position): Boolean = position == selectedQueen

    // Lazy list for Tests (allocates CellState objects)
    @get:VisibleForTesting
    val testCells: List<CellState>
        get() =
            buildList {
                for (row in 0 until boardSize) {
                    for (col in 0 until boardSize) {
                        val pos = Position(row, col)
                        add(
                            CellState(
                                position = pos,
                                hasQueen = isQueen(pos),
                                isConflicting = isConflicting(pos),
                                isAttacked = isAttacked(pos),
                                isSelected = isSelected(pos),
                            ),
                        )
                    }
                }
            }
}

@VisibleForTesting
data class CellState(
    val position: Position,
    val hasQueen: Boolean,
    val isConflicting: Boolean,
    val isAttacked: Boolean,
    val isSelected: Boolean,
)
