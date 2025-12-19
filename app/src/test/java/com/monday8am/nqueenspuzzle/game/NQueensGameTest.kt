package com.monday8am.nqueenspuzzle.game

import com.monday8am.nqueenspuzzle.models.Difficulty
import com.monday8am.nqueenspuzzle.models.Position
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NQueensGameTest {
    // ==================== Initialization ====================

    @Test
    fun `initial state is valid`() {
        val game = NQueensGame()

        with(game.state.value) {
            assertTrue("Board should start empty", queens.isEmpty())
            assertNull("No queen should be selected initially", selectedQueen)
            assertNull("Timer should not start until move is made", gameStartTime)
            assertNull("Game end time should be null", gameEndTime)
            assertEquals("Default size should be 8", 8, config.boardSize) // Assuming 8 is default
        }
    }

    @Test
    fun `initial board matches custom config`() {
        val config = GameConfig(boardSize = 6, difficulty = Difficulty.MEDIUM)
        val game = NQueensGame(initialConfig = config)

        with(game.state.value.config) {
            assertEquals(6, boardSize)
            assertEquals(Difficulty.MEDIUM, difficulty)
        }
    }

    // ==================== User Moves ====================

    @Test
    fun `userMove places and removes queens correctly`() {
        val game = NQueensGame()
        val p00 = pos(0, 0)

        // 1. Place
        game.userMove(p00)
        with(game.state.value) {
            assertTrue(queens.contains(p00))
            assertEquals(p00, selectedQueen)
            assertNotNull("Timer should start on first move", gameStartTime)
        }

        // 2. Remove (Toggle)
        game.userMove(p00)
        with(game.state.value) {
            assertFalse(queens.contains(p00))
            assertNull("Selection should clear when queen removed", selectedQueen)
        }
    }

    @Test
    fun `userMove moves a conflicting selected queen`() {
        val game = NQueensGame()
        val pStart = pos(0, 0)
        val pConflict = pos(0, 3)
        val pTarget = pos(2, 5)

        // Setup: Place two queens on same row. The second one (pConflict) becomes selected.
        game.placeQueens(pStart, pConflict)

        // Act: Move the selected queen (pConflict) to a safe spot (pTarget)
        game.userMove(pTarget)

        with(game.state.value) {
            assertFalse("Old position should be empty", queens.contains(pConflict))
            assertTrue("New position should have queen", queens.contains(pTarget))
            assertTrue("Non-selected queen remains", queens.contains(pStart))
        }
    }

    // ==================== Win Condition ====================

    @Test
    fun `game detects solved state`() {
        val game = NQueensGame(initialConfig = GameConfig(boardSize = 4))

        game.placeQueens(*SOLVED_4X4_MOVES)

        with(game.state.value) {
            assertTrue("Game should be marked solved", isSolved)
            assertNotNull("End time should be recorded", gameEndTime)
        }
    }

    @Test
    fun `moves are ignored after game is solved`() {
        val game = NQueensGame(initialConfig = GameConfig(boardSize = 4))
        game.placeQueens(*SOLVED_4X4_MOVES)

        assertTrue(game.state.value.isSolved)

        // Attempt to corrupt the board
        game.userMove(SOLVED_4X4_MOVES.first())

        with(game.state.value) {
            assertTrue("Game remains solved", isSolved)
            assertTrue("Queen was not removed", queens.contains(SOLVED_4X4_MOVES.first()))
        }
    }

    // ==================== Restart ====================

    @Test
    fun `restart resets board and timer`() {
        val game = NQueensGame()
        game.placeQueens(pos(0, 0), pos(1, 2))

        // Ensure timer started
        assertNotNull(game.state.value.gameStartTime)

        game.restart()

        with(game.state.value) {
            assertTrue(queens.isEmpty())
            assertNull(gameStartTime)
            assertNull(gameEndTime)
            assertFalse(isSolved)
        }
    }

    @Test
    fun `restart handles config changes`() {
        // 1. Restart with same config (implicit)
        val gameHard = NQueensGame(initialConfig = GameConfig(difficulty = Difficulty.HARD))
        gameHard.restart()
        assertEquals(Difficulty.HARD, gameHard.state.value.config.difficulty)

        // 2. Restart with new config
        val gameResizable = NQueensGame(initialConfig = GameConfig(boardSize = 8))
        gameResizable.restart(newConfig = GameConfig(boardSize = 4))

        with(gameResizable.state.value) {
            assertEquals("Board size should update", 4, config.boardSize)
            assertTrue("Queens should be cleared", queens.isEmpty())
        }
    }

    // ==================== Difficulty Visibility Logic ====================

    @Test
    fun `EASY difficulty visual aids`() {
        val game = NQueensGame(initialConfig = GameConfig(difficulty = Difficulty.EASY))
        val p1 = pos(0, 0)
        val p2 = pos(0, 3) // Row conflict

        // 1. Check Conflicts
        game.placeQueens(p1, p2)
        with(game.state.value) {
            assertTrue(visibleConflicts.contains(p1))
            assertTrue(visibleConflicts.contains(p2))
        }

        // 2. Check Attacked Cells (requires selection)
        game.userMove(p1) // Select p1
        assertTrue(
            "EASY should show attacked cells",
            game.state.value.visibleAttackedCells
                .isNotEmpty(),
        )
    }

    @Test
    fun `MEDIUM difficulty visual aids`() {
        val game = NQueensGame(initialConfig = GameConfig(difficulty = Difficulty.MEDIUM))

        game.userMove(pos(0, 0)) // Place and select

        assertTrue(
            "MEDIUM should NOT show attacked cells",
            game.state.value.visibleAttackedCells
                .isEmpty(),
        )
        // Note: Logic for MEDIUM conflicts implies it behaves like EASY regarding conflicts,
        // or HARD regarding attacked cells. Based on original test, it just hides attacked cells.
    }

    @Test
    fun `HARD difficulty visual aids`() {
        val game = NQueensGame(initialConfig = GameConfig(difficulty = Difficulty.HARD))
        val p1 = pos(0, 0)
        val p2 = pos(0, 3) // Row conflict

        game.placeQueens(p1, p2) // p2 is now selected because it was placed last

        with(game.state.value) {
            assertTrue("Selected conflicting queen is marked", visibleConflicts.contains(p2))
            assertFalse("Unselected conflicting queen is HIDDEN", visibleConflicts.contains(p1))
        }
    }

    // ==================== Helpers ====================

    // Short helper to create Positions
    private fun pos(
        r: Int,
        c: Int,
    ) = Position(r, c)

    // Helper to simulate multiple moves
    private fun NQueensGame.placeQueens(vararg positions: Position) {
        positions.forEach { this.userMove(it) }
    }

    companion object {
        // Solution for 4x4 board: (0,1), (1,3), (2,0), (3,2)
        private val SOLVED_4X4_MOVES =
            arrayOf(
                Position(0, 1),
                Position(1, 3),
                Position(2, 0),
                Position(3, 2),
            )
    }
}
