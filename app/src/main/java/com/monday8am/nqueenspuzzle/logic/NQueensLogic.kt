package com.monday8am.nqueenspuzzle.logic

import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.CellState
import com.monday8am.nqueenspuzzle.models.Position
import kotlin.math.abs

object NQueensLogic {

    fun hasConflict(a: Position, b: Position): Boolean {
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

    fun getAttackedCells(queen: Position, boardSize: Int): Set<Position> {
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

    fun isSolved(queens: Set<Position>, boardSize: Int): Boolean {
        if (queens.size != boardSize) return false
        return findConflictingQueens(queens).isEmpty()
    }

    fun buildBoardRenderState(
        boardSize: Int,
        queens: Set<Position>,
        selectedQueen: Position?,
        hintPosition: Position? = null
    ): BoardRenderState {
        val conflictingQueens = findConflictingQueens(queens)
        val attackedCells = selectedQueen?.let { getAttackedCells(it, boardSize) } ?: emptySet()
        val elapsedTimeMs = System.currentTimeMillis()

        val cells = mutableListOf<CellState>()
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val position = Position(row, col)
                val hasQueen = position in queens
                val isConflicting = hasQueen && position in conflictingQueens
                val isAttacked = position in attackedCells && !hasQueen
                val isHint = position == hintPosition && !hasQueen
                val isLightSquare = (row + col) % 2 == 0

                cells.add(
                    CellState(
                        position = position,
                        hasQueen = hasQueen,
                        isConflicting = isConflicting,
                        isAttacked = isAttacked,
                        isHint = isHint,
                        isLightSquare = isLightSquare
                    )
                )
            }
        }

        return BoardRenderState(
            boardSize = boardSize,
            cells = cells,
            queensRemaining = boardSize - queens.size,
            isSolved = isSolved(queens, boardSize),
            elapsedTimeMs = System.currentTimeMillis() - elapsedTimeMs,
        )
    }

    fun getHint(queens: Set<Position>, boardSize: Int): Position? {
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val pos = Position(row, col)
                if (pos !in queens && queens.none { hasConflict(pos, it) }) {
                    return pos
                }
            }
        }
        return null
    }

    fun getSolution(): Set<Position> {
        // TODO not implemented yet!
        // Evaluate the closest solution?
        return emptySet()
    }
}
