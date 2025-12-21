package com.monday8am.nqueenspuzzle.ui.game.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.monday8am.nqueenspuzzle.logic.models.Difficulty
import com.monday8am.nqueenspuzzle.logic.models.Position
import com.monday8am.nqueenspuzzle.ui.game.BoardRenderState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class GameBoardUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `test initial state for 8 x 8 board`() {
        val boardSize = 8
        val boardRenderTest = BoardRenderState(
            boardSize = boardSize,
            difficulty = Difficulty.EASY,
            queensRemaining = boardSize,
            queens = emptySet(),
            selectedQueen = null,
            isSolved = false,
        )


        composeTestRule.setContent {
            GameBoard(
                state = boardRenderTest,
                onCellTap = { }
            )
        }

        composeTestRule
            .onNodeWithText("♛ Queens remaining: $boardSize")
            .assertExists()
    }

    @Test
    fun `tapping a cell on CanvasChessBoard triggers onCellTap`() {
        var clickedPosition: Position? = null
        val boardSize = sampleBoardRenderState.boardSize

        composeTestRule.setContent {
            GameBoard(
                state = sampleBoardRenderState,
                onCellTap = { clickedPosition = it }
            )
        }

        composeTestRule
            .onNodeWithTag("chess_board")
            .performTouchInput {
                // To click cell (2, 3) - row 2, col 3
                // We click at proportional coordinates
                val cellW = width.toFloat() / boardSize
                val cellH = height.toFloat() / boardSize
                click(Offset(x = cellW * 3.5f, y = cellH * 2.5f))
            }

        assertEquals(Position(2, 3), clickedPosition)
    }

    @Test
    fun `workflow - solving a 4x4 puzzle by placing all queens correctly`() {
        // Track all tapped positions in order
        val tappedPositions = mutableListOf<Position>()

        // Solution for 4x4 board: (0,1), (1,3), (2,0), (3,2)
        val solution = listOf(Position(0, 1), Position(1, 3), Position(2, 0), Position(3, 2))

        val solvedState = BoardRenderState(
            boardSize = 4,
            difficulty = Difficulty.EASY,
            queensRemaining = 0,
            queens = solution.toSet(),
            selectedQueen = null,
            visibleConflicts = emptySet(),
            visibleAttackedCells = emptySet(),
            isSolved = true,
            calculationTime = 10L
        )

        composeTestRule.setContent {
            GameBoard(
                state = solvedState,
                onCellTap = { tappedPositions.add(it) }
            )
        }

        // Verify the board displays "Queens remaining: 0"
        composeTestRule
            .onNodeWithText("♛ Queens remaining: 0")
            .assertExists()

        // Tap each cell in the solution to verify interaction still works
        solution.forEach { position ->
            composeTestRule
                .onNodeWithTag("chess_board")
                .performTouchInput {
                    val cellW = width.toFloat() / 4
                    val cellH = height.toFloat() / 4
                    click(Offset(
                        x = cellW * (position.col + 0.5f),
                        y = cellH * (position.row + 0.5f)
                    ))
                }
        }

        // Verify all taps were registered
        assertEquals(solution, tappedPositions)
    }

    @Test
    fun `workflow - placing queens with conflicts`() {
        val tappedPositions = mutableListOf<Position>()

        // Create a state with conflicting queens in the same row
        val conflictState = BoardRenderState(
            boardSize = 8,
            difficulty = Difficulty.EASY,
            queensRemaining = 5,
            queens = setOf(Position(0, 0), Position(0, 4)), // Same row = conflict
            selectedQueen = null,
            visibleConflicts = setOf(Position(0, 0), Position(0, 4)),
            visibleAttackedCells = emptySet(),
            isSolved = false,
            calculationTime = 10L
        )

        composeTestRule.setContent {
            GameBoard(
                state = conflictState,
                onCellTap = { tappedPositions.add(it) }
            )
        }

        // Verify the board shows 5 queens remaining
        composeTestRule
            .onNodeWithText("♛ Queens remaining: 5")
            .assertExists()

        // Tap one of the conflicting positions
        composeTestRule
            .onNodeWithTag("chess_board")
            .performTouchInput {
                val cellW = width.toFloat() / 8
                val cellH = height.toFloat() / 8
                click(Offset(x = cellW * 0.5f, y = cellH * 0.5f))
            }

        // Verify the tap was registered
        assertEquals(Position(0, 0), tappedPositions.first())
    }
}
