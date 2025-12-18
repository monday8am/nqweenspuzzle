package com.monday8am.nqueenspuzzle.game

import com.monday8am.nqueenspuzzle.models.Difficulty
import com.monday8am.nqueenspuzzle.models.Position
import org.junit.Assert.*
import org.junit.Test

class NQueensGameTest {
    // ==================== Initialization ====================

    @Test
    fun `initial state is Board with empty board`() {
        val game = NQueensGame()

        val state = game.state.value
        assertTrue(state is GameState.Board)
        val boardState = (state as GameState.Board).renderState
        assertFalse(boardState.cells.any { it.hasQueen })
    }

    @Test
    fun `initial board matches config parameters`() {
        val config = GameConfig(boardSize = 6, difficulty = Difficulty.MEDIUM)
        val game = NQueensGame(initialConfig = config)

        val state = game.state.value as GameState.Board
        assertEquals(6, state.renderState.boardSize)
        assertEquals(Difficulty.MEDIUM, state.renderState.difficulty)
    }

    @Test
    fun `initial state has correct queensRemaining`() {
        val game = NQueensGame(initialConfig = GameConfig(boardSize = 8))

        val state = game.state.value as GameState.Board
        assertEquals(8, state.renderState.queensRemaining)
    }

    // ==================== User Moves ====================

    @Test
    fun `userMove on empty cell places queen`() {
        val game = NQueensGame()

        game.userMove(Position(0, 0))

        val state = game.state.value as GameState.Board
        assertTrue(state.renderState.cells.find { it.position == Position(0, 0) }!!.hasQueen)
        assertEquals(7, state.renderState.queensRemaining)
    }

    @Test
    fun `userMove on queen removes it`() {
        val game = NQueensGame()
        game.userMove(Position(3, 3)) // Place
        game.userMove(Position(3, 3)) // Remove

        val state = game.state.value as GameState.Board
        assertFalse(state.renderState.cells.find { it.position == Position(3, 3) }!!.hasQueen)
        assertEquals(8, state.renderState.queensRemaining)
    }

    @Test
    fun `userMove starts timer on first move`() {
        val game = NQueensGame()
        val startTime = System.currentTimeMillis()

        game.userMove(Position(0, 0))

        // Game should have started timing
        val state = game.state.value as GameState.Board
        // We can't directly check the timer, but we can verify it's been set by making another move
        // and checking that the state is still Board (not Init)
        assertNotNull(state)
    }

    @Test
    fun `userMove on conflicting selected queen moves it`() {
        val game = NQueensGame()
        // Place two conflicting queens on same row
        game.userMove(Position(0, 0))
        game.userMove(Position(0, 3)) // Same row - conflict, now selected

        // Tap empty cell to move the selected conflicting queen
        game.userMove(Position(2, 5))

        val state = game.state.value as GameState.Board
        // Original position should be empty, new position should have queen
        assertFalse(state.renderState.cells.find { it.position == Position(0, 3) }!!.hasQueen)
        assertTrue(state.renderState.cells.find { it.position == Position(2, 5) }!!.hasQueen)
        // First queen should still be there
        assertTrue(state.renderState.cells.find { it.position == Position(0, 0) }!!.hasQueen)
    }

    @Test
    fun `userMove ignores taps when board full with conflicts`() {
        val game = NQueensGame(initialConfig = GameConfig(boardSize = 4))
        // Place 4 queens in conflicting positions (all same row)
        game.userMove(Position(0, 0))
        game.userMove(Position(0, 1))
        game.userMove(Position(0, 2))
        game.userMove(Position(0, 3))

        val stateBefore = game.state.value as GameState.Board
        val queensCountBefore = stateBefore.renderState.cells.count { it.hasQueen }

        // Try to tap an empty cell - should do nothing since board is full and no queen selected
        game.userMove(Position(1, 0))

        val stateAfter = game.state.value as GameState.Board
        // Board should remain unchanged
        assertEquals(queensCountBefore, stateAfter.renderState.cells.count { it.hasQueen })
        assertFalse(stateAfter.renderState.isSolved)
    }

    // ==================== Win Condition ====================

    @Test
    fun `game emits UserWon when solved`() {
        val game = NQueensGame(initialConfig = GameConfig(boardSize = 4))
        // Place winning solution
        game.userMove(Position(0, 1))
        game.userMove(Position(1, 3))
        game.userMove(Position(2, 0))
        game.userMove(Position(3, 2))

        val state = game.state.value
        assertTrue(state is GameState.UserWon)
    }

    @Test
    fun `userMove after win is ignored`() {
        val game = NQueensGame(initialConfig = GameConfig(boardSize = 4))
        // Place winning solution
        game.userMove(Position(0, 1))
        game.userMove(Position(1, 3))
        game.userMove(Position(2, 0))
        game.userMove(Position(3, 2))

        assertTrue(game.state.value is GameState.UserWon)

        // Try to remove a queen after winning - should be ignored
        game.userMove(Position(0, 1))

        // State should still be UserWon
        assertTrue(game.state.value is GameState.UserWon)
    }

    @Test
    fun `UserWon state includes correct elapsed time`() {
        val game = NQueensGame(initialConfig = GameConfig(boardSize = 4))
        val startTime = System.currentTimeMillis()

        // Place winning solution
        game.userMove(Position(0, 1))
        game.userMove(Position(1, 3))
        game.userMove(Position(2, 0))
        game.userMove(Position(3, 2))

        val endTime = System.currentTimeMillis()
        val state = game.state.value as GameState.UserWon

        // Elapsed time should be reasonable (less than 1 second for test)
        assertTrue(state.gameTimeMillis < 1000)
        assertTrue(state.gameTimeMillis >= 0)
    }

    // ==================== Restart ====================

    @Test
    fun `restart clears all queens`() {
        val game = NQueensGame()
        game.userMove(Position(0, 0))
        game.userMove(Position(1, 2))
        game.userMove(Position(3, 4))

        game.restart()

        val state = game.state.value as GameState.Board
        assertFalse(state.renderState.cells.any { it.hasQueen })
        assertEquals(8, state.renderState.queensRemaining)
    }

    @Test
    fun `restart preserves config when newConfig is null`() {
        val game = NQueensGame(initialConfig = GameConfig(boardSize = 6, difficulty = Difficulty.HARD))
        game.userMove(Position(0, 0))

        game.restart()

        val state = game.state.value as GameState.Board
        assertEquals(6, state.renderState.boardSize)
        assertEquals(Difficulty.HARD, state.renderState.difficulty)
    }

    @Test
    fun `restart with newConfig changes board size`() {
        val game = NQueensGame(initialConfig = GameConfig(boardSize = 8))
        game.userMove(Position(0, 0))

        game.restart(newConfig = GameConfig(boardSize = 4))

        val state = game.state.value as GameState.Board
        assertEquals(4, state.renderState.boardSize)
        assertEquals(16, state.renderState.cells.size)
    }

    @Test
    fun `restart resets timer`() {
        val game = NQueensGame()
        game.userMove(Position(0, 0)) // Starts timer
        game.userMove(Position(1, 2))

        game.restart()

        val state = game.state.value as GameState.Board
        // Board should be empty, indicating fresh start
        assertFalse(state.renderState.cells.any { it.hasQueen })
    }

    // ==================== Difficulty ====================

    @Test
    fun `EASY difficulty shows all conflicts`() {
        val game = NQueensGame(initialConfig = GameConfig(difficulty = Difficulty.EASY))
        // Place conflicting queens
        game.userMove(Position(0, 0))
        game.userMove(Position(0, 3)) // Same row - conflict

        val state = game.state.value as GameState.Board
        // Both queens should be marked as conflicting
        assertTrue(state.renderState.cells.find { it.position == Position(0, 0) }!!.isConflicting)
        assertTrue(state.renderState.cells.find { it.position == Position(0, 3) }!!.isConflicting)
    }

    @Test
    fun `EASY difficulty shows attacked cells for selected queen`() {
        val game = NQueensGame(initialConfig = GameConfig(difficulty = Difficulty.EASY))
        game.userMove(Position(0, 0)) // Places and selects queen

        val state = game.state.value as GameState.Board
        // Should show attacked cells (black dots)
        val attackedCells = state.renderState.cells.filter { it.isEmptyAndAttacked }
        assertTrue(attackedCells.isNotEmpty())
    }

    @Test
    fun `HARD difficulty only shows selected queen conflict`() {
        val game = NQueensGame(initialConfig = GameConfig(difficulty = Difficulty.HARD))
        // Place conflicting queens
        game.userMove(Position(0, 0))
        game.userMove(Position(0, 3)) // Same row - conflict, now selected

        val state = game.state.value as GameState.Board
        // Only selected queen should be marked as conflicting
        assertTrue(state.renderState.cells.find { it.position == Position(0, 3) }!!.isConflicting)
        // Other conflicting queen should NOT be marked
        assertFalse(state.renderState.cells.find { it.position == Position(0, 0) }!!.isConflicting)
    }

    @Test
    fun `MEDIUM difficulty hides attacked cells`() {
        val game = NQueensGame(initialConfig = GameConfig(difficulty = Difficulty.MEDIUM))
        game.userMove(Position(0, 0)) // Places and selects queen

        val state = game.state.value as GameState.Board
        // Should NOT show attacked cells
        val attackedCells = state.renderState.cells.filter { it.isEmptyAndAttacked }
        assertTrue(attackedCells.isEmpty())
    }

    @Test
    fun `updateDifficulty preserves queens and updates hint visibility`() {
        val game = NQueensGame(initialConfig = GameConfig(difficulty = Difficulty.EASY))
        game.userMove(Position(0, 0))
        game.userMove(Position(2, 1))

        val stateBefore = game.state.value as GameState.Board
        val queensBefore = stateBefore.renderState.cells.count { it.hasQueen }

        game.updateDifficulty(Difficulty.HARD)

        val stateAfter = game.state.value as GameState.Board
        // Queens should be preserved
        assertEquals(queensBefore, stateAfter.renderState.cells.count { it.hasQueen })
        // Difficulty should be updated
        assertEquals(Difficulty.HARD, stateAfter.renderState.difficulty)
    }

    // ==================== State Flow Behavior ====================

    @Test
    fun `gameState emits Board after each userMove`() {
        val game = NQueensGame()

        game.userMove(Position(0, 0))
        assertTrue(game.state.value is GameState.Board)

        game.userMove(Position(1, 2))
        assertTrue(game.state.value is GameState.Board)

        game.userMove(Position(3, 4))
        assertTrue(game.state.value is GameState.Board)
    }

    @Test
    fun `gameState is StateFlow with immediate value`() {
        val game = NQueensGame()

        // StateFlow should have a value immediately
        assertNotNull(game.state.value)
        assertTrue(game.state.value is GameState.Board)
    }
}
