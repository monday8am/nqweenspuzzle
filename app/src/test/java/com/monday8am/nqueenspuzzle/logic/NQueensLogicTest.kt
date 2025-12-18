package com.monday8am.nqueenspuzzle.logic

import com.monday8am.nqueenspuzzle.models.Position
import org.junit.Assert.*
import org.junit.Test

class NQueensLogicTest {
    // ==================== hasConflict tests ====================

    @Test
    fun `hasConflict returns false for same position`() {
        val pos = Position(3, 3)
        assertFalse(NQueensLogic.hasConflict(pos, pos))
    }

    @Test
    fun `hasConflict detects same row`() {
        val a = Position(2, 0)
        val b = Position(2, 5)
        assertTrue(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict detects same column`() {
        val a = Position(0, 3)
        val b = Position(7, 3)
        assertTrue(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict detects main diagonal (top-left to bottom-right)`() {
        val a = Position(1, 1)
        val b = Position(4, 4)
        assertTrue(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict detects anti-diagonal (top-right to bottom-left)`() {
        val a = Position(1, 5)
        val b = Position(4, 2)
        assertTrue(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict detects diagonal going up-right`() {
        val a = Position(5, 2)
        val b = Position(3, 4)
        assertTrue(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict returns false for non-conflicting positions`() {
        val a = Position(0, 0)
        val b = Position(1, 2) // Knight's move - no conflict
        assertFalse(NQueensLogic.hasConflict(a, b))
    }

    @Test
    fun `hasConflict returns false for another non-conflicting pair`() {
        val a = Position(2, 3)
        val b = Position(5, 1)
        assertFalse(NQueensLogic.hasConflict(a, b))
    }

    // ==================== findConflictingQueens tests ====================

    @Test
    fun `findConflictingQueens returns empty for empty board`() {
        val result = NQueensLogic.findConflictingQueens(emptySet())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findConflictingQueens returns empty for single queen`() {
        val queens = setOf(Position(3, 3))
        val result = NQueensLogic.findConflictingQueens(queens)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findConflictingQueens returns empty for non-conflicting queens`() {
        // Two queens that don't attack each other
        val queens = setOf(Position(0, 0), Position(2, 1))
        val result = NQueensLogic.findConflictingQueens(queens)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findConflictingQueens detects row conflict`() {
        val q1 = Position(2, 0)
        val q2 = Position(2, 5)
        val queens = setOf(q1, q2)
        val result = NQueensLogic.findConflictingQueens(queens)
        assertEquals(setOf(q1, q2), result)
    }

    @Test
    fun `findConflictingQueens detects column conflict`() {
        val q1 = Position(1, 3)
        val q2 = Position(6, 3)
        val queens = setOf(q1, q2)
        val result = NQueensLogic.findConflictingQueens(queens)
        assertEquals(setOf(q1, q2), result)
    }

    @Test
    fun `findConflictingQueens detects diagonal conflict`() {
        val q1 = Position(0, 0)
        val q2 = Position(3, 3)
        val queens = setOf(q1, q2)
        val result = NQueensLogic.findConflictingQueens(queens)
        assertEquals(setOf(q1, q2), result)
    }

    @Test
    fun `findConflictingQueens returns only conflicting queens from mixed set`() {
        val conflicting1 = Position(0, 0)
        val conflicting2 = Position(0, 5) // Same row as conflicting1
        val safe = Position(2, 1) // Not conflicting with either (knight's move from 0,0)
        val queens = setOf(conflicting1, conflicting2, safe)
        val result = NQueensLogic.findConflictingQueens(queens)
        assertEquals(setOf(conflicting1, conflicting2), result)
    }

    @Test
    fun `findConflictingQueens handles multiple conflicts`() {
        // Three queens all in same row
        val q1 = Position(2, 0)
        val q2 = Position(2, 3)
        val q3 = Position(2, 7)
        val queens = setOf(q1, q2, q3)
        val result = NQueensLogic.findConflictingQueens(queens)
        assertEquals(setOf(q1, q2, q3), result)
    }

    // ==================== getAttackedCells tests ====================

    @Test
    fun `getAttackedCells returns correct cells for corner queen`() {
        val queen = Position(0, 0)
        val attacked = NQueensLogic.getAttackedCells(queen, 4)

        // Row 0: (0,1), (0,2), (0,3)
        assertTrue(Position(0, 1) in attacked)
        assertTrue(Position(0, 2) in attacked)
        assertTrue(Position(0, 3) in attacked)

        // Column 0: (1,0), (2,0), (3,0)
        assertTrue(Position(1, 0) in attacked)
        assertTrue(Position(2, 0) in attacked)
        assertTrue(Position(3, 0) in attacked)

        // Diagonal: (1,1), (2,2), (3,3)
        assertTrue(Position(1, 1) in attacked)
        assertTrue(Position(2, 2) in attacked)
        assertTrue(Position(3, 3) in attacked)

        // Queen's own position should NOT be included
        assertFalse(Position(0, 0) in attacked)
    }

    @Test
    fun `getAttackedCells returns correct cells for center queen`() {
        val queen = Position(2, 2)
        val attacked = NQueensLogic.getAttackedCells(queen, 5)

        // Row 2
        assertTrue(Position(2, 0) in attacked)
        assertTrue(Position(2, 1) in attacked)
        assertTrue(Position(2, 3) in attacked)
        assertTrue(Position(2, 4) in attacked)

        // Column 2
        assertTrue(Position(0, 2) in attacked)
        assertTrue(Position(1, 2) in attacked)
        assertTrue(Position(3, 2) in attacked)
        assertTrue(Position(4, 2) in attacked)

        // Main diagonal
        assertTrue(Position(0, 0) in attacked)
        assertTrue(Position(1, 1) in attacked)
        assertTrue(Position(3, 3) in attacked)
        assertTrue(Position(4, 4) in attacked)

        // Anti-diagonal
        assertTrue(Position(0, 4) in attacked)
        assertTrue(Position(1, 3) in attacked)
        assertTrue(Position(3, 1) in attacked)
        assertTrue(Position(4, 0) in attacked)
    }

    @Test
    fun `getAttackedCells does not include positions outside board`() {
        val queen = Position(0, 0)
        val attacked = NQueensLogic.getAttackedCells(queen, 4)

        // No negative positions
        attacked.forEach { pos ->
            assertTrue(pos.row >= 0)
            assertTrue(pos.col >= 0)
            assertTrue(pos.row < 4)
            assertTrue(pos.col < 4)
        }
    }

    @Test
    fun `getAttackedCells count is correct for 8x8 board center`() {
        val queen = Position(3, 3)
        val attacked = NQueensLogic.getAttackedCells(queen, 8)

        // Row: 7 cells, Column: 7 cells, Diagonals vary
        // For center-ish position, should be significant coverage
        assertTrue(attacked.size > 20)
    }

    // ==================== isSolved tests ====================

    @Test
    fun `isSolved returns false for empty board`() {
        assertFalse(NQueensLogic.isSolved(emptySet(), 8))
    }

    @Test
    fun `isSolved returns false for incomplete board`() {
        val queens = setOf(Position(0, 0), Position(1, 2))
        assertFalse(NQueensLogic.isSolved(queens, 8))
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
        assertFalse(NQueensLogic.isSolved(queens, 4))
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
        assertTrue(NQueensLogic.isSolved(queens, 4))
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
        assertTrue(NQueensLogic.isSolved(queens, 8))
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
        assertTrue(NQueensLogic.isSolved(queens, 8))
    }

    // ==================== buildBoardRenderState tests ====================

    @Test
    fun `buildBoardRenderState creates correct number of cells`() {
        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 8,
                queens = emptySet(),
                selectedQueen = null,
            )
        assertEquals(64, state.cells.size)
    }

    @Test
    fun `buildBoardRenderState creates correct number of cells for 4x4`() {
        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 4,
                queens = emptySet(),
                selectedQueen = null,
            )
        assertEquals(16, state.cells.size)
    }

    @Test
    fun `buildBoardRenderState marks queens correctly`() {
        val queens = setOf(Position(0, 0), Position(2, 3))
        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 4,
                queens = queens,
                selectedQueen = null,
            )

        val queenCells = state.cells.filter { it.hasQueen }
        assertEquals(2, queenCells.size)
        assertTrue(queenCells.any { it.position == Position(0, 0) })
        assertTrue(queenCells.any { it.position == Position(2, 3) })
    }

    @Test
    fun `buildBoardRenderState marks conflicting queens`() {
        val q1 = Position(0, 0)
        val q2 = Position(0, 3) // Same row - conflict
        val queens = setOf(q1, q2)

        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 4,
                queens = queens,
                selectedQueen = null,
            )

        val conflictingCells = state.cells.filter { it.isConflicting }
        assertEquals(2, conflictingCells.size)
    }

    @Test
    fun `buildBoardRenderState marks attacked cells when queen selected`() {
        val queen = Position(0, 0)
        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 4,
                queens = setOf(queen),
                selectedQueen = queen,
            )

        val attackedCells = state.cells.filter { it.isAttacked }
        assertTrue(attackedCells.isNotEmpty())
        // Queen's cell should not be marked as attacked
        assertFalse(state.cells.find { it.position == queen }!!.isAttacked)
    }

    @Test
    fun `buildBoardRenderState does not mark attacked cells when no queen selected`() {
        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 4,
                queens = setOf(Position(0, 0)),
                selectedQueen = null,
            )

        val attackedCells = state.cells.filter { it.isAttacked }
        assertTrue(attackedCells.isEmpty())
    }

    @Test
    fun `buildBoardRenderState calculates light squares correctly`() {
        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 4,
                queens = emptySet(),
                selectedQueen = null,
            )

        // (0,0) should be light
        assertTrue(state.cells.find { it.position == Position(0, 0) }!!.isLightSquare)
        // (0,1) should be dark
        assertFalse(state.cells.find { it.position == Position(0, 1) }!!.isLightSquare)
        // (1,1) should be light
        assertTrue(state.cells.find { it.position == Position(1, 1) }!!.isLightSquare)
    }

    @Test
    fun `buildBoardRenderState calculates queensRemaining correctly`() {
        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 8,
                queens = setOf(Position(0, 0), Position(1, 2), Position(2, 4)),
                selectedQueen = null,
            )
        assertEquals(5, state.queensRemaining)
    }

    @Test
    fun `buildBoardRenderState sets isSolved correctly for solved puzzle`() {
        val solution =
            setOf(
                Position(0, 1),
                Position(1, 3),
                Position(2, 0),
                Position(3, 2),
            )
        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 4,
                queens = solution,
                selectedQueen = null,
            )
        assertTrue(state.isSolved)
        assertEquals(0, state.queensRemaining)
    }

    @Test
    fun `buildBoardRenderState sets isSolved false for unsolved puzzle`() {
        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 4,
                queens = setOf(Position(0, 0)),
                selectedQueen = null,
            )
        assertFalse(state.isSolved)
    }

    // ==================== getHint tests ====================

    @Test
    fun `getHint returns first valid position on empty board`() {
        val hint = NQueensLogic.getHint(emptySet(), 4)
        assertEquals(Position(0, 0), hint)
    }

    @Test
    fun `getHint skips occupied cells`() {
        val queens = setOf(Position(0, 0))
        val hint = NQueensLogic.getHint(queens, 4)
        // (0,1) conflicts with (0,0) same row, (1,0) conflicts same col, (1,1) conflicts diagonal
        // First non-conflicting is (0,2)? No, same row. (1,2)? No same diagonal offset
        // Actually (2,1) should be valid
        assertNotNull(hint)
        assertNotEquals(Position(0, 0), hint)
        // Verify it doesn't conflict with existing queens
        assertTrue(queens.none { NQueensLogic.hasConflict(hint!!, it) })
    }

    @Test
    fun `getHint returns null when no valid position exists`() {
        // Fill a 4x4 board such that no valid position remains (impossible scenario for valid puzzle)
        // For testing, place 4 queens in same row (conflicts but fills positions)
        val queens =
            setOf(
                Position(0, 0),
                Position(1, 1),
                Position(2, 2),
                Position(3, 3),
            )
        val hint = NQueensLogic.getHint(queens, 4)
        // All positions are either occupied or attacked by diagonal queens
        assertNull(hint)
    }

    @Test
    fun `getHint returns valid position that does not conflict`() {
        val queens = setOf(Position(0, 1), Position(1, 3))
        val hint = NQueensLogic.getHint(queens, 4)
        assertNotNull(hint)
        // Verify hint doesn't conflict with any queen
        assertTrue(queens.none { NQueensLogic.hasConflict(hint!!, it) })
    }

    @Test
    fun `buildBoardRenderState marks hint cell correctly`() {
        val hintPosition = Position(2, 2)
        val state =
            NQueensLogic.buildBoardRenderState(
                boardSize = 4,
                queens = emptySet(),
                selectedQueen = null,
                hintPosition = hintPosition,
            )

        val hintCell = state.cells.find { it.position == hintPosition }
        assertNotNull(hintCell)
        assertTrue(hintCell!!.isHint)

        // Other cells should not be hints
        val nonHintCells = state.cells.filter { it.position != hintPosition }
        assertTrue(nonHintCells.none { it.isHint })
    }
}
