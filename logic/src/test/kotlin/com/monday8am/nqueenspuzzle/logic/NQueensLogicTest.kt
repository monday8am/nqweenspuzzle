package com.monday8am.nqueenspuzzle.logic

import com.monday8am.nqueenspuzzle.logic.models.Position
import org.junit.Assert
import org.junit.Test

class NQueensLogicTest {
    // ==================== hasConflict tests ====================

    @Test
    fun `hasConflict returns false for same position`() {
        val pos = Position(3, 3)
        Assert.assertFalse(NQueensLogic.hasConflict(pos, pos))
    }

    @Test
    fun `hasConflict detects same row`() {
        val a = Position(2, 0)
        val b = Position(2, 5)
        Assert.assertTrue(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict detects same column`() {
        val a = Position(0, 3)
        val b = Position(7, 3)
        Assert.assertTrue(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict detects main diagonal (top-left to bottom-right)`() {
        val a = Position(1, 1)
        val b = Position(4, 4)
        Assert.assertTrue(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict detects anti-diagonal (top-right to bottom-left)`() {
        val a = Position(1, 5)
        val b = Position(4, 2)
        Assert.assertTrue(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict detects diagonal going up-right`() {
        val a = Position(5, 2)
        val b = Position(3, 4)
        Assert.assertTrue(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict returns false for non-conflicting positions`() {
        val a = Position(0, 0)
        val b = Position(1, 2) // Knight's move - no conflict
        Assert.assertFalse(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict returns false for another non-conflicting pair`() {
        val a = Position(2, 3)
        val b = Position(5, 1)
        Assert.assertFalse(NQueensLogic.hasConflict(a, b))
    }

    // ==================== findConflictingQueens tests ====================

    @Test
    fun `findConflictingQueens returns empty for empty board`() {
        val result = NQueensLogic.findConflictingQueens(emptySet())
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `findConflictingQueens returns empty for single queen`() {
        val queens = setOf(Position(3, 3))
        val result = NQueensLogic.findConflictingQueens(queens)
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `findConflictingQueens returns empty for non-conflicting queens`() {
        // Two queens that don't attack each other
        val queens = setOf(Position(0, 0), Position(2, 1))
        val result = NQueensLogic.findConflictingQueens(queens)
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `findConflictingQueens detects row conflict`() {
        val q1 = Position(2, 0)
        val q2 = Position(2, 5)
        val queens = setOf(q1, q2)
        val result = NQueensLogic.findConflictingQueens(queens)
        Assert.assertEquals(setOf(q1, q2), result)
    }

    @Test
    fun `findConflictingQueens detects column conflict`() {
        val q1 = Position(1, 3)
        val q2 = Position(6, 3)
        val queens = setOf(q1, q2)
        val result = NQueensLogic.findConflictingQueens(queens)
        Assert.assertEquals(setOf(q1, q2), result)
    }

    @Test
    fun `findConflictingQueens detects diagonal conflict`() {
        val q1 = Position(0, 0)
        val q2 = Position(3, 3)
        val queens = setOf(q1, q2)
        val result = NQueensLogic.findConflictingQueens(queens)
        Assert.assertEquals(setOf(q1, q2), result)
    }

    @Test
    fun `findConflictingQueens returns only conflicting queens from mixed set`() {
        val conflicting1 = Position(0, 0)
        val conflicting2 = Position(0, 5) // Same row as conflicting1
        val safe = Position(2, 1) // Not conflicting with either (knight's move from 0,0)
        val queens = setOf(conflicting1, conflicting2, safe)
        val result = NQueensLogic.findConflictingQueens(queens)
        Assert.assertEquals(setOf(conflicting1, conflicting2), result)
    }

    @Test
    fun `findConflictingQueens handles multiple conflicts`() {
        // Three queens all in same row
        val q1 = Position(2, 0)
        val q2 = Position(2, 3)
        val q3 = Position(2, 7)
        val queens = setOf(q1, q2, q3)
        val result = NQueensLogic.findConflictingQueens(queens)
        Assert.assertEquals(setOf(q1, q2, q3), result)
    }

    // ==================== getAttackedCells tests ====================

    @Test
    fun `getAttackedCells returns correct cells for corner queen`() {
        val queen = Position(0, 0)
        val attacked = NQueensLogic.getAttackedCells(queen, 4)

        // Row 0: (0,1), (0,2), (0,3)
        Assert.assertTrue(Position(0, 1) in attacked)
        Assert.assertTrue(Position(0, 2) in attacked)
        Assert.assertTrue(Position(0, 3) in attacked)

        // Column 0: (1,0), (2,0), (3,0)
        Assert.assertTrue(Position(1, 0) in attacked)
        Assert.assertTrue(Position(2, 0) in attacked)
        Assert.assertTrue(Position(3, 0) in attacked)

        // Diagonal: (1,1), (2,2), (3,3)
        Assert.assertTrue(Position(1, 1) in attacked)
        Assert.assertTrue(Position(2, 2) in attacked)
        Assert.assertTrue(Position(3, 3) in attacked)

        // Queen's own position should NOT be included
        Assert.assertFalse(Position(0, 0) in attacked)
    }

    @Test
    fun `getAttackedCells returns correct cells for center queen`() {
        val queen = Position(2, 2)
        val attacked = NQueensLogic.getAttackedCells(queen, 5)

        // Row 2
        Assert.assertTrue(Position(2, 0) in attacked)
        Assert.assertTrue(Position(2, 1) in attacked)
        Assert.assertTrue(Position(2, 3) in attacked)
        Assert.assertTrue(Position(2, 4) in attacked)

        // Column 2
        Assert.assertTrue(Position(0, 2) in attacked)
        Assert.assertTrue(Position(1, 2) in attacked)
        Assert.assertTrue(Position(3, 2) in attacked)
        Assert.assertTrue(Position(4, 2) in attacked)

        // Main diagonal
        Assert.assertTrue(Position(0, 0) in attacked)
        Assert.assertTrue(Position(1, 1) in attacked)
        Assert.assertTrue(Position(3, 3) in attacked)
        Assert.assertTrue(Position(4, 4) in attacked)

        // Anti-diagonal
        Assert.assertTrue(Position(0, 4) in attacked)
        Assert.assertTrue(Position(1, 3) in attacked)
        Assert.assertTrue(Position(3, 1) in attacked)
        Assert.assertTrue(Position(4, 0) in attacked)
    }

    @Test
    fun `getAttackedCells does not include positions outside board`() {
        val queen = Position(0, 0)
        val attacked = NQueensLogic.getAttackedCells(queen, 4)

        // No negative positions
        attacked.forEach { pos ->
            Assert.assertTrue(pos.row >= 0)
            Assert.assertTrue(pos.col >= 0)
            Assert.assertTrue(pos.row < 4)
            Assert.assertTrue(pos.col < 4)
        }
    }

    @Test
    fun `getAttackedCells count is correct for 8x8 board center`() {
        val queen = Position(3, 3)
        val attacked = NQueensLogic.getAttackedCells(queen, 8)

        // Row: 7 cells, Column: 7 cells, Diagonals vary
        // For center-ish position, should be significant coverage
        Assert.assertTrue(attacked.size > 20)
    }

    // ==================== isSolved tests ====================

    @Test
    fun `isSolved returns false for empty board`() {
        Assert.assertFalse(NQueensLogic.isSolved(emptySet(), 8))
    }

    @Test
    fun `isSolved returns false for incomplete board`() {
        val queens = setOf(Position(0, 0), Position(1, 2))
        Assert.assertFalse(NQueensLogic.isSolved(queens, 8))
    }

    @Test
    fun `isSolved returns false for correct count but with conflicts`() {
        // 4 queens on a 4x4 board, but all in same row
        val queens =
            setOf(
                Position(0, 0),
                Position(0, 1),
                Position(0, 2),
                Position(0, 3),
            )
        Assert.assertFalse(NQueensLogic.isSolved(queens, 4))
    }

    @Test
    fun `isSolved returns true for valid 4-queens solution`() {
        // Known valid 4-queens solution
        val queens =
            setOf(
                Position(0, 1),
                Position(1, 3),
                Position(2, 0),
                Position(3, 2),
            )
        Assert.assertTrue(NQueensLogic.isSolved(queens, 4))
    }

    @Test
    fun `isSolved returns true for valid 8-queens solution`() {
        // Known valid 8-queens solution
        val queens =
            setOf(
                Position(0, 0),
                Position(1, 4),
                Position(2, 7),
                Position(3, 5),
                Position(4, 2),
                Position(5, 6),
                Position(6, 1),
                Position(7, 3),
            )
        Assert.assertTrue(NQueensLogic.isSolved(queens, 8))
    }

    @Test
    fun `isSolved returns true for another valid 8-queens solution`() {
        // Another known valid 8-queens solution
        val queens =
            setOf(
                Position(0, 3),
                Position(1, 1),
                Position(2, 6),
                Position(3, 2),
                Position(4, 5),
                Position(5, 7),
                Position(6, 4),
                Position(7, 0),
            )
        Assert.assertTrue(NQueensLogic.isSolved(queens, 8))
    }
}
