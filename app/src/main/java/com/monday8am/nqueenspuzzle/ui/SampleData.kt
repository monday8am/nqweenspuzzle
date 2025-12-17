package com.monday8am.nqueenspuzzle.ui

import com.monday8am.nqueenspuzzle.models.CellState
import com.monday8am.nqueenspuzzle.models.Position



private const val boardSize = 8
private val queenPositions = listOf(Position(0, 0), Position(1, 2), Position(0, 4)) // (0,0) and (0,4) conflict

// Define attacked positions manually for the preview
private val attackedPositions = setOf(
    // Queen at (0,0)
    Position(0, 1), Position(0,2), Position(0,3), Position(0,4), Position(0,5), Position(0,6), Position(0,7),
    Position(1,0), Position(2,0), Position(3,0), Position(4,0), Position(5,0), Position(6,0), Position(7,0),
    Position(1,1), Position(2,2), Position(3,3), Position(4,4), Position(5,5), Position(6,6), Position(7,7),
    // Queen at (1,2)
    Position(1,0), Position(1,1), Position(1,3), Position(1,4), Position(1,5), Position(1,6), Position(1,7),
    Position(0,2), Position(2,2), Position(3,2), Position(4,2), Position(5,2), Position(6,2), Position(7,2),
    Position(0,1), Position(2,3), Position(3,4), Position(4,5), Position(5,6), Position(6,7),
    Position(2,1), Position(3,0), Position(0,3),
    // Queen at (0,4)
    Position(1,4), Position(2,4), Position(3,4), Position(4,4), Position(5,4), Position(6,4), Position(7,4),
    Position(1,3), Position(2,2), Position(3,1), Position(4,0),
    Position(1,5), Position(2,6), Position(3,7)
)

internal val cells = (0 until boardSize).flatMap { row ->
    (0 until boardSize).map { col ->
        val position = Position(row, col)
        val hasQueen = queenPositions.contains(position)
        CellState(
            position = position,
            hasQueen = hasQueen,
            isAttacked = attackedPositions.contains(position) && !hasQueen,
            isConflicting = (position == Position(0, 0) || position == Position(0, 4)), // Mark conflicting queens
            isLightSquare = (row + col) % 2 != 0
        )
    }
}
