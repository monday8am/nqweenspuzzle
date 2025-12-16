package com.monday8am.nqueenspuzzle.models

data class BoardRenderState(
    val boardSize: Int,
    val cells: List<CellState>,
    val queensRemaining: Int,
    val isSolved: Boolean,
    val elapsedTimeMs: Long
)
