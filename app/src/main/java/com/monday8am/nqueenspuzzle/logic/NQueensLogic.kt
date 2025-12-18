package com.monday8am.nqueenspuzzle.logic

import com.monday8am.nqueenspuzzle.models.Position
import kotlin.math.abs

/**
 * Pure game logic for the N-Queens puzzle.
 * Contains only game rules - no presentation/UI concerns.
 */
object NQueensLogic {
    fun hasConflict(
        a: Position,
        b: Position,
    ): Boolean {
        if (a == b) return false
        val sameRow = a.row == b.row
        val sameCol = a.col == b.col
        val sameDiagonal = abs(a.row - b.row) == abs(a.col - b.col)
        return sameRow || sameCol || sameDiagonal
    }

    fun findConflictingQueens(queens: Set<Position>): Set<Position> {
        val conflicting = mutableSetOf<Position>()
        val queenList = queens.toList()
        for (i in queenList.indices) {
            for (j in i + 1 until queenList.size) {
                if (hasConflict(queenList[i], queenList[j])) {
                    conflicting.add(queenList[i])
                    conflicting.add(queenList[j])
                }
            }
        }
        return conflicting
    }

    fun getAttackedCells(
        queen: Position,
        boardSize: Int,
    ): Set<Position> {
        val attacked = mutableSetOf<Position>()

        for (i in 0 until boardSize) {
            // Same row
            if (i != queen.col) {
                attacked.add(Position(queen.row, i))
            }
            // Same column
            if (i != queen.row) {
                attacked.add(Position(i, queen.col))
            }
        }

        // Diagonals
        for (offset in 1 until boardSize) {
            // Top-left to bottom-right diagonal
            if (queen.row - offset >= 0 && queen.col - offset >= 0) {
                attacked.add(Position(queen.row - offset, queen.col - offset))
            }
            if (queen.row + offset < boardSize && queen.col + offset < boardSize) {
                attacked.add(Position(queen.row + offset, queen.col + offset))
            }
            // Top-right to bottom-left diagonal
            if (queen.row - offset >= 0 && queen.col + offset < boardSize) {
                attacked.add(Position(queen.row - offset, queen.col + offset))
            }
            if (queen.row + offset < boardSize && queen.col - offset >= 0) {
                attacked.add(Position(queen.row + offset, queen.col - offset))
            }
        }

        return attacked
    }

    fun isSolved(
        queens: Set<Position>,
        boardSize: Int,
    ): Boolean {
        if (queens.size != boardSize) return false
        return findConflictingQueens(queens).isEmpty()
    }
}
