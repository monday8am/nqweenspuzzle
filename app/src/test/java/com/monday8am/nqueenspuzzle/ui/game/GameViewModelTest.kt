package com.monday8am.nqueenspuzzle.ui.game

import com.monday8am.nqueenspuzzle.logic.models.Difficulty
import com.monday8am.nqueenspuzzle.logic.models.Position
import com.monday8am.nqueenspuzzle.ui.game.UserAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private lateinit var viewModel: GameViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GameViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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

    // ==================== TapCell - Placing Queens ====================

    @Test
    fun `tapping empty cell places a queen`() {
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))

        val state = viewModel.renderState.value
        assertTrue(state.cells.find { it.position == Position(0, 0) }!!.hasQueen)
        assertEquals(7, state.queensRemaining)
    }

    @Test
    fun `can place multiple queens`() {
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(2, 1)))
        viewModel.dispatch(UserAction.TapCell(Position(4, 3)))

        val state = viewModel.renderState.value
        val queenCells = state.cells.filter { it.hasQueen }
        assertEquals(3, queenCells.size)
        assertEquals(5, state.queensRemaining)
    }

    // ==================== TapCell - Auto-Select on Place ====================

    @Test
    fun `placing queen auto-selects it and shows attacked cells`() {
        viewModel.dispatch(UserAction.TapCell(Position(3, 3))) // Place queen (auto-selects)

        val state = viewModel.renderState.value
        val attackedCells = state.cells.filter { it.isAttacked }
        assertTrue(attackedCells.isNotEmpty())
    }

    @Test
    fun `placing different queen changes selection and attacked cells`() {
        viewModel.dispatch(UserAction.TapCell(Position(0, 0))) // Place first queen (selected)

        val state1 = viewModel.renderState.value
        val attacked1 =
            state1.cells
                .filter { it.isAttacked }
                .map { it.position }
                .toSet()

        viewModel.dispatch(UserAction.TapCell(Position(7, 7))) // Place second queen (now selected)

        val state2 = viewModel.renderState.value
        val attacked2 =
            state2.cells
                .filter { it.isAttacked }
                .map { it.position }
                .toSet()

        assertNotEquals(attacked1, attacked2)
    }

    // ==================== TapCell - Removing Queens ====================

    @Test
    fun `tapping queen removes it`() {
        viewModel.dispatch(UserAction.TapCell(Position(3, 3))) // Place
        viewModel.dispatch(UserAction.TapCell(Position(3, 3))) // Remove (tapping existing queen)

        val state = viewModel.renderState.value
        assertFalse(state.cells.find { it.position == Position(3, 3) }!!.hasQueen)
        assertEquals(8, state.queensRemaining)
    }

    @Test
    fun `removing queen clears attacked cells`() {
        viewModel.dispatch(UserAction.TapCell(Position(3, 3))) // Place (auto-selects, shows attacked)
        viewModel.dispatch(UserAction.TapCell(Position(3, 3))) // Remove

        val state = viewModel.renderState.value
        assertFalse(state.cells.any { it.isAttacked })
    }

    // ==================== Conflict Detection ====================

    @Test
    fun `conflicting queens are marked`() {
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(0, 5))) // Same row - conflict

        val state = viewModel.renderState.value
        assertTrue(state.cells.find { it.position == Position(0, 0) }!!.isConflicting)
        assertTrue(state.cells.find { it.position == Position(0, 5) }!!.isConflicting)
    }

    @Test
    fun `non-conflicting queens are not marked`() {
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(2, 1))) // No conflict

        val state = viewModel.renderState.value
        assertFalse(state.cells.find { it.position == Position(0, 0) }!!.isConflicting)
        assertFalse(state.cells.find { it.position == Position(2, 1) }!!.isConflicting)
    }

    // ==================== Reset ====================

    @Test
    fun `reset clears all queens`() {
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(1, 2)))
        viewModel.dispatch(UserAction.TapCell(Position(3, 4)))
        viewModel.dispatch(UserAction.Reset)

        val state = viewModel.renderState.value
        assertFalse(state.cells.any { it.hasQueen })
        assertEquals(8, state.queensRemaining)
    }

    @Test
    fun `reset preserves board size`() {
        viewModel.dispatch(UserAction.SetBoardSize(4))
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))
        viewModel.dispatch(UserAction.Reset)

        val state = viewModel.renderState.value
        assertEquals(4, state.boardSize)
        assertEquals(16, state.cells.size)
    }

    @Test
    fun `reset clears selection`() {
        viewModel.dispatch(UserAction.TapCell(Position(3, 3)))
        viewModel.dispatch(UserAction.TapCell(Position(3, 3))) // Select
        viewModel.dispatch(UserAction.Reset)

        val state = viewModel.renderState.value
        assertFalse(state.cells.any { it.isAttacked })
    }

    // ==================== SetBoardSize ====================

    @Test
    fun `setBoardSize changes board dimensions`() {
        viewModel.dispatch(UserAction.SetBoardSize(4))

        val state = viewModel.renderState.value
        assertEquals(4, state.boardSize)
        assertEquals(16, state.cells.size)
    }

    @Test
    fun `setBoardSize clears existing queens`() {
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(1, 2)))
        viewModel.dispatch(UserAction.SetBoardSize(6))

        val state = viewModel.renderState.value
        assertFalse(state.cells.any { it.hasQueen })
        assertEquals(6, state.queensRemaining)
    }

    @Test
    fun `setBoardSize updates queensRemaining`() {
        viewModel.dispatch(UserAction.SetBoardSize(5))
        assertEquals(5, viewModel.renderState.value.queensRemaining)

        viewModel.dispatch(UserAction.SetBoardSize(4))
        assertEquals(4, viewModel.renderState.value.queensRemaining)
    }

    // ==================== Solved State ====================

    @Test
    fun `isSolved true when valid solution placed`() {
        viewModel.dispatch(UserAction.SetBoardSize(4))
        // Known 4-queens solution
        viewModel.dispatch(UserAction.TapCell(Position(0, 1)))
        viewModel.dispatch(UserAction.TapCell(Position(1, 3)))
        viewModel.dispatch(UserAction.TapCell(Position(2, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(3, 2)))

        val state = viewModel.renderState.value
        assertTrue(state.isSolved)
        assertEquals(0, state.queensRemaining)
    }

    @Test
    fun `isSolved false when queens have conflicts`() {
        viewModel.dispatch(UserAction.SetBoardSize(4))
        // Invalid - all in same row
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(0, 1)))
        viewModel.dispatch(UserAction.TapCell(Position(0, 2)))
        viewModel.dispatch(UserAction.TapCell(Position(0, 3)))

        val state = viewModel.renderState.value
        assertFalse(state.isSolved)
    }

    // ==================== SetDifficulty ====================

    @Test
    fun `setDifficulty changes difficulty level`() {
        viewModel.dispatch(UserAction.SetDifficulty(Difficulty.HARD))

        val state = viewModel.renderState.value
        assertEquals(Difficulty.HARD, state.difficulty)
    }

    @Test
    fun `setDifficulty resets existing queens`() {
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(2, 1)))
        viewModel.dispatch(UserAction.SetDifficulty(Difficulty.MEDIUM))

        val state = viewModel.renderState.value
        assertEquals(0, state.cells.count { it.hasQueen })
        assertEquals(8, state.queensRemaining)
    }

    @Test
    fun `setDifficulty preserves board size`() {
        viewModel.dispatch(UserAction.SetBoardSize(6))
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))
        viewModel.dispatch(UserAction.SetDifficulty(Difficulty.HARD))

        val state = viewModel.renderState.value
        assertEquals(6, state.boardSize)
        assertEquals(36, state.cells.size)
    }

    @Test
    fun `initial state has EASY difficulty`() {
        val state = viewModel.renderState.value
        assertEquals(Difficulty.EASY, state.difficulty)
    }

    // ==================== TapCell - Moving Queens ====================

    @Test
    fun `tapping empty cell moves conflicting selected queen`() {
        // Place two conflicting queens on same row
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(0, 3))) // Same row - conflict, now selected

        // Tap empty cell to move the selected conflicting queen
        viewModel.dispatch(UserAction.TapCell(Position(2, 5)))

        val state = viewModel.renderState.value
        // Original position should be empty, new position should have queen
        assertFalse(state.cells.find { it.position == Position(0, 3) }!!.hasQueen)
        assertTrue(state.cells.find { it.position == Position(2, 5) }!!.hasQueen)
        // First queen should still be there
        assertTrue(state.cells.find { it.position == Position(0, 0) }!!.hasQueen)
        assertEquals(6, state.queensRemaining)
    }

    @Test
    fun `tapping empty cell does not move non-conflicting selected queen`() {
        // Place a single queen (no conflict)
        viewModel.dispatch(UserAction.TapCell(Position(0, 0))) // Selected, no conflict

        // Try to move by tapping empty cell - should add new queen instead
        viewModel.dispatch(UserAction.TapCell(Position(2, 1)))

        val state = viewModel.renderState.value
        // Both positions should have queens (original not moved, new one added)
        assertTrue(state.cells.find { it.position == Position(0, 0) }!!.hasQueen)
        assertTrue(state.cells.find { it.position == Position(2, 1) }!!.hasQueen)
        assertEquals(6, state.queensRemaining)
    }

    // ==================== TapCell - Game Won ====================

    @Test
    fun `tapping cell after game won is ignored`() {
        viewModel.dispatch(UserAction.SetBoardSize(4))
        // Place winning solution
        viewModel.dispatch(UserAction.TapCell(Position(0, 1)))
        viewModel.dispatch(UserAction.TapCell(Position(1, 3)))
        viewModel.dispatch(UserAction.TapCell(Position(2, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(3, 2)))

        assertTrue(viewModel.renderState.value.isSolved)

        // Try to remove a queen after winning - should be ignored
        viewModel.dispatch(UserAction.TapCell(Position(0, 1)))

        val state = viewModel.renderState.value
        // Queen should still be there
        assertTrue(state.cells.find { it.position == Position(0, 1) }!!.hasQueen)
        assertTrue(state.isSolved)
    }

    // ==================== TapCell - Board Full ====================

    @Test
    fun `tapping empty cell when board full does nothing`() {
        viewModel.dispatch(UserAction.SetBoardSize(4))
        // Place 4 queens (filling the board)
        viewModel.dispatch(UserAction.TapCell(Position(0, 1)))
        viewModel.dispatch(UserAction.TapCell(Position(1, 3)))
        viewModel.dispatch(UserAction.TapCell(Position(2, 0)))
        viewModel.dispatch(UserAction.TapCell(Position(3, 2)))

        // Try to tap an empty cell - should do nothing since board is full and solved
        val stateBefore = viewModel.renderState.value
        viewModel.dispatch(UserAction.TapCell(Position(0, 0)))

        val stateAfter = viewModel.renderState.value
        // Board should remain solved with same queens
        assertEquals(stateBefore.queensRemaining, stateAfter.queensRemaining)
        assertTrue(stateAfter.isSolved)
    }
}
