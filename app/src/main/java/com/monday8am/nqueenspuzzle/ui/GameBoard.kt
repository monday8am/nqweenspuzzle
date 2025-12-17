package com.monday8am.nqueenspuzzle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.CellState
import com.monday8am.nqueenspuzzle.models.Position

private val LightSquareColor = Color(0xFFF0D9B5)
private val DarkSquareColor = Color(0xFFB58863)
private val AttackedLightColor = Color(0xFFE8C4A0)
private val AttackedDarkColor = Color(0xFFA07855)
private val ConflictColor = Color(0xFFE53935)
private val QueenColor = Color(0xFF1B1B1B)

@Composable
fun GameBoard(
    state: BoardRenderState,
    onCellTap: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(state.boardSize),
        modifier = modifier
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
        }
    }
}
