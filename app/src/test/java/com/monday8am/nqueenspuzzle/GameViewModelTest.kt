package com.monday8am.nqueenspuzzle

import com.monday8am.nqueenspuzzle.models.Position
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameViewModelTest {

    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        viewModel = GameViewModel()
    }

    // ==================== Initial State ====================

    @Test
    fun `initial state has 8x8 board`() {
        val state = viewModel.renderState.value
        assertEquals(8, state.boardSize)
    }

    @Test
    fun `initial state has no queens`() {
        val state = viewModel.renderState.value
        assertEquals(8, state.queensRemaining)
        assertFalse(state.cells.any { it.hasQueen })
    }

    @Test
    fun `initial state is not solved`() {
        val state = viewModel.renderState.value
        assertFalse(state.isSolved)
    }

    @Test
    fun `initial state has 64 cells`() {
        val state = viewModel.renderState.value
        assertEquals(64, state.cells.size)
    }

    // ==================== onCellTap - Placing Queens ====================

    @Test
    fun `tapping empty cell places a queen`() {
        viewModel.onCellTap(Position(0, 0))

        val state = viewModel.renderState.value
        assertTrue(state.cells.find { it.position == Position(0, 0) }!!.hasQueen)
        assertEquals(7, state.queensRemaining)
    }

    @Test
    fun `can place multiple queens`() {
        viewModel.onCellTap(Position(0, 0))
        viewModel.onCellTap(Position(2, 1))
        viewModel.onCellTap(Position(4, 3))

        val state = viewModel.renderState.value
        val queenCells = state.cells.filter { it.hasQueen }
        assertEquals(3, queenCells.size)
        assertEquals(5, state.queensRemaining)
    }

    // ==================== onCellTap - Selecting Queens ====================

    @Test
    fun `tapping queen selects it and shows attacked cells`() {
        viewModel.onCellTap(Position(3, 3)) // Place queen
        viewModel.onCellTap(Position(3, 3)) // Select it

        val state = viewModel.renderState.value
        val attackedCells = state.cells.filter { it.isAttacked }
        assertTrue(attackedCells.isNotEmpty())
    }

    @Test
    fun `selecting different queen changes attacked cells`() {
        viewModel.onCellTap(Position(0, 0)) // Place first queen
        viewModel.onCellTap(Position(7, 7)) // Place second queen
        viewModel.onCellTap(Position(0, 0)) // Select first

        val state1 = viewModel.renderState.value
        val attacked1 = state1.cells.filter { it.isAttacked }.map { it.position }.toSet()

        viewModel.onCellTap(Position(7, 7)) // Select second

        val state2 = viewModel.renderState.value
        val attacked2 = state2.cells.filter { it.isAttacked }.map { it.position }.toSet()

        assertNotEquals(attacked1, attacked2)
    }

    // ==================== onCellTap - Removing Queens ====================

    @Test
    fun `tapping selected queen removes it`() {
        viewModel.onCellTap(Position(3, 3)) // Place
        viewModel.onCellTap(Position(3, 3)) // Select
        viewModel.onCellTap(Position(3, 3)) // Remove

        val state = viewModel.renderState.value
        assertFalse(state.cells.find { it.position == Position(3, 3) }!!.hasQueen)
        assertEquals(8, state.queensRemaining)
    }

    @Test
    fun `removing queen clears attacked cells`() {
        viewModel.onCellTap(Position(3, 3)) // Place
        viewModel.onCellTap(Position(3, 3)) // Select
        viewModel.onCellTap(Position(3, 3)) // Remove

        val state = viewModel.renderState.value
        assertFalse(state.cells.any { it.isAttacked })
    }

    // ==================== Conflict Detection ====================

    @Test
    fun `conflicting queens are marked`() {
        viewModel.onCellTap(Position(0, 0))
        viewModel.onCellTap(Position(0, 5)) // Same row - conflict

        val state = viewModel.renderState.value
        assertTrue(state.cells.find { it.position == Position(0, 0) }!!.isConflicting)
        assertTrue(state.cells.find { it.position == Position(0, 5) }!!.isConflicting)
    }

    @Test
    fun `non-conflicting queens are not marked`() {
        viewModel.onCellTap(Position(0, 0))
        viewModel.onCellTap(Position(2, 1)) // No conflict

        val state = viewModel.renderState.value
        assertFalse(state.cells.find { it.position == Position(0, 0) }!!.isConflicting)
        assertFalse(state.cells.find { it.position == Position(2, 1) }!!.isConflicting)
    }

    // ==================== reset ====================

    @Test
    fun `reset clears all queens`() {
        viewModel.onCellTap(Position(0, 0))
        viewModel.onCellTap(Position(1, 2))
        viewModel.onCellTap(Position(3, 4))
        viewModel.reset()

        val state = viewModel.renderState.value
        assertFalse(state.cells.any { it.hasQueen })
        assertEquals(8, state.queensRemaining)
    }

    @Test
    fun `reset preserves board size`() {
        viewModel.setBoardSize(4)
        viewModel.onCellTap(Position(0, 0))
        viewModel.reset()

        val state = viewModel.renderState.value
        assertEquals(4, state.boardSize)
        assertEquals(16, state.cells.size)
    }

    @Test
    fun `reset clears selection`() {
        viewModel.onCellTap(Position(3, 3))
        viewModel.onCellTap(Position(3, 3)) // Select
        viewModel.reset()

        val state = viewModel.renderState.value
        assertFalse(state.cells.any { it.isAttacked })
    }

    // ==================== setBoardSize ====================

    @Test
    fun `setBoardSize changes board dimensions`() {
        viewModel.setBoardSize(4)

        val state = viewModel.renderState.value
        assertEquals(4, state.boardSize)
        assertEquals(16, state.cells.size)
    }

    @Test
    fun `setBoardSize clears existing queens`() {
        viewModel.onCellTap(Position(0, 0))
        viewModel.onCellTap(Position(1, 2))
        viewModel.setBoardSize(6)

        val state = viewModel.renderState.value
        assertFalse(state.cells.any { it.hasQueen })
        assertEquals(6, state.queensRemaining)
    }

    @Test
    fun `setBoardSize updates queensRemaining`() {
        viewModel.setBoardSize(5)
        assertEquals(5, viewModel.renderState.value.queensRemaining)

        viewModel.setBoardSize(4)
        assertEquals(4, viewModel.renderState.value.queensRemaining)
    }

    // ==================== Solved State ====================

    @Test
    fun `isSolved true when valid solution placed`() {
        viewModel.setBoardSize(4)
        // Known 4-queens solution
        viewModel.onCellTap(Position(0, 1))
        viewModel.onCellTap(Position(1, 3))
        viewModel.onCellTap(Position(2, 0))
        viewModel.onCellTap(Position(3, 2))

        val state = viewModel.renderState.value
        assertTrue(state.isSolved)
        assertEquals(0, state.queensRemaining)
    }

    @Test
    fun `isSolved false when queens have conflicts`() {
        viewModel.setBoardSize(4)
        // Invalid - all in same row
        viewModel.onCellTap(Position(0, 0))
        viewModel.onCellTap(Position(0, 1))
        viewModel.onCellTap(Position(0, 2))
        viewModel.onCellTap(Position(0, 3))

        val state = viewModel.renderState.value
        assertFalse(state.isSolved)
    }
}
