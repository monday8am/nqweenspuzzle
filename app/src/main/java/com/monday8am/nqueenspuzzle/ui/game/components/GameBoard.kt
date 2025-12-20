package com.monday8am.nqueenspuzzle.ui.game.components

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monday8am.nqueenspuzzle.logic.models.Position
import com.monday8am.nqueenspuzzle.ui.game.BoardRenderState

internal val LightSquareColor = Color(0xFFebecd0)
internal val DarkSquareColor = Color(0xFF739552)
internal val ConflictColor = Color(0xFFE53935)
internal val QueenColor = Color(0xFF1B1B1B)
internal val markerColor = Color.Black.copy(alpha = 0.2f)
internal val attackedQueenColor = ConflictColor.copy(alpha = 0.8f)

@Composable
internal fun GameBoard(
    state: BoardRenderState,
    onCellTap: (Position) -> Unit,
    modifier: Modifier = Modifier,
    useCanvasBoard: Boolean = false,
) {
    Column(
        verticalArrangement = spacedBy(4.dp),
        modifier = modifier,
    ) {
        Text(
            text = "\u265B Queens remaining: ${state.queensRemaining}",
            style = MaterialTheme.typography.titleMedium,
        )

        if (useCanvasBoard) {
            CanvasChessBoard(
                state = state,
                onCellTap = onCellTap,
            )
        } else {
            LayoutChessBoard(
                state = state,
                onCellTap = onCellTap,
            )
        }

        Text(
            text = "Calculation time: ${state.calculationTime}ms",
            fontSize = 12.sp,
            color = Color.DarkGray,
        )
    }
}

@Preview(showBackground = true, name = "8x8 Board - In Progress")
@Composable
private fun LayoutBoardPreview() {
    GameBoard(
        state = sampleBoardRenderState,
        onCellTap = { _ -> },
        useCanvasBoard = false,
    )
}

@Preview(showBackground = true, name = "8x8 Board - Simple")
@Composable
private fun CanvasBoardPreview() {
    GameBoard(
        state = sampleBoardRenderState,
        onCellTap = { _ -> },
        useCanvasBoard = true,
    )
}

