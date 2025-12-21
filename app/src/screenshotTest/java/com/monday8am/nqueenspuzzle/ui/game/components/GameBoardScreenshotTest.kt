package com.monday8am.nqueenspuzzle.ui.game.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.monday8am.nqueenspuzzle.logic.models.Difficulty
import com.monday8am.nqueenspuzzle.logic.models.Position
import com.monday8am.nqueenspuzzle.ui.game.BoardRenderState

/**
 * Screenshot tests for GameBoard composable.
 * These previews are used to generate reference images for visual regression testing.
 */

@PreviewTest
@Preview(showBackground = true, name = "8x8 Board - In Progress")
@Composable
fun GameBoardInProgressPreview() {
    GameBoard(
        state = sampleBoardRenderState,
        onCellTap = { }
    )
}

@PreviewTest
@Preview(showBackground = true, name = "4x4 Board - Solved")
@Composable
fun GameBoardSolvedPreview() {
    GameBoard(
        state = BoardRenderState(
            boardSize = 4,
            difficulty = Difficulty.EASY,
            queensRemaining = 0,
            queens = setOf(Position(0, 1), Position(1, 3), Position(2, 0), Position(3, 2)),
            selectedQueen = null,
            visibleConflicts = emptySet(),
            visibleAttackedCells = emptySet(),
            isSolved = true,
            calculationTime = 10L
        ),
        onCellTap = { }
    )
}

@PreviewTest
@Preview(showBackground = true, name = "8x8 Board - With Conflicts")
@Composable
fun GameBoardWithConflictsPreview() {
    GameBoard(
        state = BoardRenderState(
            boardSize = 8,
            difficulty = Difficulty.EASY,
            queensRemaining = 6,
            queens = setOf(Position(0, 0), Position(0, 4)), // Same row = conflict
            selectedQueen = Position(0, 0),
            visibleConflicts = setOf(Position(0, 0), Position(0, 4)),
            visibleAttackedCells = emptySet(),
            isSolved = false,
            calculationTime = 5L
        ),
        onCellTap = { }
    )
}
