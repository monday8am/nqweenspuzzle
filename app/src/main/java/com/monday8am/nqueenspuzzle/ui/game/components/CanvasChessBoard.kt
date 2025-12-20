package com.monday8am.nqueenspuzzle.ui.game.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import com.monday8am.nqueenspuzzle.logic.models.Position
import com.monday8am.nqueenspuzzle.ui.game.CellState

@Composable
internal fun CanvasChessBoard(
    cells: List<CellState>,
    boardSize: Int,
    onCellTap: (Position) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .pointerInput(boardSize) {
                detectTapGestures { offset ->
                    val cellSize = size.width / boardSize
                    val col = (offset.x / cellSize).toInt()
                    val row = (offset.y / cellSize).toInt()
                    if (col in 0 until boardSize && row in 0 until boardSize) {
                        onCellTap(Position(row, col))
                    }
                }
            }
    ) {
        CanvasBoard(
            boardSize = boardSize,
            cells = cells,
        )

        PieceLayout(
            boardSize = boardSize,
            cells = cells,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PieceLayout(
    boardSize: Int,
    cells: List<CellState>,
    modifier: Modifier = Modifier,
) {
    // Filter only cells with queens to compose
    val activePieces = cells.filter { it.hasQueen }

    Layout(
        content = {
            activePieces.forEach { cell ->
                QueenPiece(
                    hasQueenAttacking = cell.hasQueenAttacking,
                    boardSize = boardSize,
                )
            }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val cellSize = constraints.maxWidth / boardSize
        val pieceConstraints = Constraints.fixed(cellSize, cellSize)
        val placeables = measurables.map { it.measure(pieceConstraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val cell = activePieces[index]
                val row = cell.position.row
                val col = cell.position.col

                placeable.place(
                    x = col * cellSize,
                    y = row * cellSize
                )
            }
        }
    }
}

@Composable
private fun QueenPiece(
    hasQueenAttacking: Boolean,
    boardSize: Int,
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        val queenSize =
            when {
                boardSize <= 4 -> 32.sp
                boardSize <= 6 -> 28.sp
                else -> 24.sp
            }
        Text(
            text = "\u265B",
            fontSize = queenSize,
            color = if (hasQueenAttacking) Color.White else QueenColor,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CanvasBoard(
    boardSize: Int,
    cells: List<CellState>,
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier
            .aspectRatio(1f)
            .drawWithCache {
                val cellSize = size.width / boardSize
                // Pre-calculate path/objects if needed based on size
                // For a simple grid, calculations are cheap, but we avoid allocating Color/Stroke in loop

                onDrawBehind {
                    // Draw board background (static usually, but dynamic here based on simple math)
                    drawRect(LightSquareColor)

                    for (row in 0 until boardSize) {
                        for (col in 0 until boardSize) {
                            val cellIndex = row * boardSize + col
                            val cell = cells[cellIndex]
                            val left = col * cellSize
                            val top = row * cellSize
                            val center = Offset(left + cellSize / 2, top + cellSize / 2)


                            if ((row + col) % 2 == 1) {
                                drawRect(
                                    color = DarkSquareColor,
                                    topLeft = Offset(left, top),
                                    size = Size(cellSize, cellSize)
                                )
                            }

                            if (cell.hasQueenAttacking) {
                                drawRect(
                                    color = ConflictColor,
                                    topLeft = Offset(left, top),
                                    size = Size(cellSize, cellSize)
                                )
                            }

                            if (cell.hasQueenAttacked) {
                                val strokeWidth = cellSize * 0.1f
                                val radius = (cellSize - strokeWidth) / 2
                                drawCircle(
                                    color = attackedQueenColor,
                                    radius = radius * 0.85f,
                                    center = center,
                                    style = Stroke(width = strokeWidth),
                                )
                            } else if (cell.isEmptyAndAttacked) {
                                drawCircle(
                                    color = markerColor,
                                    radius = cellSize * 0.15f,
                                    center = center,
                                    style = Fill,
                                )
                            }
                        }
                    }
                }
            }
    )
}
