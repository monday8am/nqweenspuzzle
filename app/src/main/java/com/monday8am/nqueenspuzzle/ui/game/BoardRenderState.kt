package com.monday8am.nqueenspuzzle.ui.game

import androidx.annotation.VisibleForTesting
import com.monday8am.nqueenspuzzle.logic.models.Difficulty

data class BoardRenderState(
    val boardSize: Int,
    val difficulty: Difficulty,
    val cells: List<CellState>,
    val queensRemaining: Int,
    val isSolved: Boolean,
    @get:VisibleForTesting
    val calculationTime: Long = 0L,
)
