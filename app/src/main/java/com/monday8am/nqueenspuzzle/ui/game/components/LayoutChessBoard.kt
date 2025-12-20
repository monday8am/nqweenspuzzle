package com.monday8am.nqueenspuzzle.ui.game.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monday8am.nqueenspuzzle.logic.models.Position
import com.monday8am.nqueenspuzzle.ui.game.CellState
import kotlin.collections.forEach

@Composable
internal fun LayoutChessBoard(
    cells: List<CellState>,
    boardSize: Int,
    onCellTap: (Position) -> Unit,
) {
    // Custom Layout for Fixed Grid
    Layout(
        content = {
            cells.forEach { cell ->
                Cell(
                    cell = cell,
                    boardSize = boardSize,
                    onClick = { onCellTap(cell.position) },
                )
            }
        },
        modifier =
            Modifier
                .aspectRatio(1f)
                .border(2.dp, Color.Black),
    ) { measurables, constraints ->
        val boardSize = boardSize
        val cellWidth = constraints.maxWidth / boardSize
        // Ensure strict squareness if needed, or just use width for both dimensions if it's a square board
        val cellConstraints =
            Constraints.fixed(
                width = cellWidth,
                height = cellWidth,
            )

        val placeables = measurables.map { it.measure(cellConstraints) }

        layout(constraints.maxWidth, constraints.maxWidth) {
            placeables.forEachIndexed { index, placeable ->
                val row = index / boardSize
                val col = index % boardSize
                placeable.placeRelative(
                    x = col * cellWidth,
                    y = row * cellWidth,
                )
            }
        }
    }
}

@Composable
private fun Cell(
    cell: CellState,
    boardSize: Int,
    onClick: () -> Unit,
) {
    val backgroundColor =
        when {
            cell.hasQueenAttacking -> ConflictColor
            cell.isLightSquare -> LightSquareColor
            else -> DarkSquareColor
        }

    Box(
        modifier =
            Modifier
                .aspectRatio(1f)
                .drawBehind {
                    drawRect(color = backgroundColor)
                    if (cell.hasQueenAttacked) {
                        val strokeWidth = size.width * 0.1f
                        val radius = (size.minDimension - strokeWidth) / 2
                        // We shrink the radius slightly so the stroke doesn't get clipped
                        drawCircle(
                            color = attackedQueenColor,
                            radius = radius * 0.85f,
                            style = Stroke(width = strokeWidth),
                        )
                    } else if (cell.isEmptyAndAttacked) {
                        drawCircle(
                            color = markerColor,
                            radius = size.minDimension * 0.15f,
                            style = Fill,
                        )
                    }
                }.clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (cell.hasQueen) {
            val queenSize =
                when {
                    boardSize <= 4 -> 32.sp
                    boardSize <= 6 -> 28.sp
                    else -> 24.sp
                }
            Text(
                text = "\u265B",
                fontSize = queenSize,
                color = if (cell.hasQueenAttacking) Color.White else QueenColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}

