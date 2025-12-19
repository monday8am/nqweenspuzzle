package com.monday8am.nqueenspuzzle.ui

import androidx.annotation.VisibleForTesting
import com.monday8am.nqueenspuzzle.models.Difficulty

data class BoardRenderState(
    val boardSize: Int,
    val difficulty: Difficulty,
    val cells: List<CellState>,
    val queensRemaining: Int,
    val isSolved: Boolean,
    @param:VisibleForTesting
    val calculationTime: Long,
)
