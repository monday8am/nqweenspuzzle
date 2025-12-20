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
)
