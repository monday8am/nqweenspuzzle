package com.monday8am.nqueenspuzzle.models

data class CellState(
    val position: Position,
    val hasQueen: Boolean,
    val isConflicting: Boolean,
    val isAttacked: Boolean,
    val isHint: Boolean,
    val isLightSquare: Boolean
)
