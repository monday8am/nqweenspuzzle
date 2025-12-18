package com.monday8am.nqueenspuzzle.models

import androidx.annotation.VisibleForTesting

data class BoardRenderState(
    val boardSize: Int,
    val cells: List<CellState>,
    val queensRemaining: Int,
    val isSolved: Boolean,
    @param:VisibleForTesting
    val processingTime: Long,
)
