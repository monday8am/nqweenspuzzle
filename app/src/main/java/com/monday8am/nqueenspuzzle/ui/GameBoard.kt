package com.monday8am.nqueenspuzzle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.CellState
import com.monday8am.nqueenspuzzle.models.Position

private val LightSquareColor = Color(0xFFebecd0)
private val DarkSquareColor = Color(0xFF739552)
private val AttackedLightColor = LightSquareColor
private val AttackedDarkColor = DarkSquareColor
private val ConflictColor = Color(0xFFE53935)
private val QueenColor = Color(0xFF1B1B1B)

@Composable
fun GameBoard(
    state: BoardRenderState,
    onCellTap: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = spacedBy(4.dp),
        modifier = modifier
    ) {
        Text(
            text = "\u265B Queens remaining: ${state.queensRemaining}",
            style = MaterialTheme.typography.titleMedium
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(state.boardSize),
            modifier = Modifier
                .aspectRatio(1f)
                .border(2.dp, Color.Black)
        ) {
            items(state.cells) { cell ->
                Cell(
                    cell = cell,
                    boardSize = state.boardSize,
                    onClick = { onCellTap(cell.position) }
                )
            }
        }
        Text(
            text ="Calculation time: ${state.calculationTime}ms",
            fontSize = 12.sp,
            color = Color.DarkGray,
        )
    }

}

@Composable
private fun Cell(
    cell: CellState,
    boardSize: Int,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        cell.isConflicting -> ConflictColor
        cell.isAttacked && cell.isLightSquare -> AttackedLightColor
        cell.isAttacked && !cell.isLightSquare -> AttackedDarkColor
        cell.isLightSquare -> LightSquareColor
        else -> DarkSquareColor
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (cell.hasQueen) {
            val queenSize = when {
                boardSize <= 4 -> 32.sp
                boardSize <= 6 -> 28.sp
                else -> 24.sp
            }
            Text(
                text = "\u265B",
                fontSize = queenSize,
                color = if (cell.isConflicting) Color.White else QueenColor,
                textAlign = TextAlign.Center
            )
        } else if (cell.isAttacked) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Attacked",
                tint = Color.Black.copy(alpha = 0.2f),
            )
        }
    }
}

@Preview(showBackground = true, name = "8x8 Board - In Progress")
@Composable
private fun GameBoardPreviewInProgress() {
    val inProgressState = BoardRenderState(
        boardSize = 8,
        cells = cells,
        queensRemaining = 5, // 8 - 3
        isSolved = false,
        calculationTime = 45L // e.g., 45 seconds
    )

    GameBoard(
        state = inProgressState,
        onCellTap = { _ -> },
    )
}
