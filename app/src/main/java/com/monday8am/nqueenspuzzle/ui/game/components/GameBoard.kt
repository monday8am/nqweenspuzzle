package com.monday8am.nqueenspuzzle.ui.game.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monday8am.nqueenspuzzle.logic.models.Difficulty
import com.monday8am.nqueenspuzzle.logic.models.Position
import com.monday8am.nqueenspuzzle.ui.game.BoardRenderState
import com.monday8am.nqueenspuzzle.ui.game.CellState

private val LightSquareColor = Color(0xFFebecd0)
private val DarkSquareColor = Color(0xFF739552)
private val ConflictColor = Color(0xFFE53935)
private val QueenColor = Color(0xFF1B1B1B)
private val markerColor = Color.Black.copy(alpha = 0.2f)
private val attackedQueenColor = ConflictColor.copy(alpha = 0.8f)

@Composable
fun GameBoard(
    state: BoardRenderState,
    onCellTap: (Position) -> Unit,
    modifier: Modifier = Modifier,
    useCanvasBoard: Boolean = true,
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
            NewChessBoard(
                cells = state.cells,
                boardSize = state.boardSize,
                onCellTap = onCellTap,
            )
        } else {
            ChessBoard(
                cells = state.cells,
                boardSize = state.boardSize,
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

@Composable
private fun ChessBoard(
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
private fun NewChessBoard(
    cells: List<CellState>,
    boardSize: Int,
    onCellTap: (Position) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.aspectRatio(1f)) {
        CanvasBoard(
            boardSize = boardSize,
            cells = cells,
        )

        FixedGrid(
            size = boardSize,
            modifier = Modifier.fillMaxSize()
        ) {
            for (row in 0 until boardSize) {
                for (col in 0 until boardSize) {
                    val cell = cells[row * boardSize + col]
                    SimpleCell(
                        hasQueen = cell.hasQueen,
                        hasQueenAttacking = cell.hasQueenAttacking,
                        boardSize = boardSize,
                        onClick = { onCellTap(Position(row, col)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FixedGrid(
    size: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val cellSize = minOf(constraints.maxWidth, constraints.maxHeight) / size
        val cellConstraints = Constraints.fixed(cellSize, cellSize)

        val placeables = measurables.map { it.measure(cellConstraints) }

        layout(cellSize * size, cellSize * size) {
            placeables.forEachIndexed { index, placeable ->
                val row = index / size
                val col = index % size
                placeable.place(col * cellSize, row * cellSize)
            }
        }
    }
}

@Composable
private fun CanvasBoard(
    boardSize: Int,
    cells: List<CellState>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.aspectRatio(1f)) {
        val cellSize = size.width / boardSize

        // Draw all light squares as background
        drawRect(LightSquareColor)

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val cell = cells[row * boardSize + col]
                val offset = Offset(col * cellSize, row * cellSize)
                val dotOffset = Offset(offset.x + cellSize / 2, offset.y + cellSize / 2)

                if ((row + col) % 2 == 1) {
                    drawRect(
                        color = DarkSquareColor,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }

                if (cell.hasQueenAttacking) {
                    drawRect(
                        color = ConflictColor,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }

                if (cell.hasQueenAttacked) {
                    val strokeWidth = cellSize * 0.1f
                    val radius = (cellSize - strokeWidth) / 2
                    // We shrink the radius slightly so the stroke doesn't get clipped
                    drawCircle(
                        color = attackedQueenColor,
                        radius = radius * 0.85f,
                        center = dotOffset,
                        style = Stroke(width = strokeWidth),
                    )
                } else if (cell.isEmptyAndAttacked) {
                    drawCircle(
                        color = markerColor,
                        radius = cellSize * 0.15f,
                        center = dotOffset,
                        style = Fill,
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleCell(
    hasQueen: Boolean,
    hasQueenAttacking: Boolean,
    boardSize: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (hasQueen) {
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

private val inProgressState =
    BoardRenderState(
        boardSize = 8,
        difficulty = Difficulty.EASY,
        cells = cells,
        queensRemaining = 5,
        isSolved = false,
        calculationTime = 45L,
    )

@Preview(showBackground = true, name = "8x8 Board - In Progress")
@Composable
private fun GameBoardPreviewInProgress() {
    GameBoard(
        state = inProgressState,
        onCellTap = { _ -> },
        useCanvasBoard = false,
    )
}

@Preview(showBackground = true, name = "8x8 Board - Simple")
@Composable
private fun CanvasBoardPreview() {
    GameBoard(
        state = inProgressState,
        onCellTap = { _ -> },
        useCanvasBoard = true,
    )
}

