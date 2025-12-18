package com.monday8am.nqueenspuzzle.models

data class CellState(
    val position: Position,
    val hasQueen: Boolean,
    val isConflicting: Boolean,
    val isAttacked: Boolean,
    val isLightSquare: Boolean,
    val isSelected: Boolean,
) {
    val isEmptyAndAttacked = !hasQueen && isAttacked
    val hasQueenAttacked = hasQueen && isConflicting && !isSelected
    val hasQueenAttacking = isConflicting && isSelected
}

