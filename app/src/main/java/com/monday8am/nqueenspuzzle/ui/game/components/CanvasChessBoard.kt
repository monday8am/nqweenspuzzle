package com.monday8am.nqueenspuzzle.ui.game.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import com.monday8am.nqueenspuzzle.R
import com.monday8am.nqueenspuzzle.logic.models.Position
import com.monday8am.nqueenspuzzle.ui.game.BoardRenderState
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
internal fun CanvasChessBoard(
    state: BoardRenderState,
    onCellTap: (Position) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .testTag("chess_board")
                .pointerInput(state.boardSize) {
                    detectTapGestures { offset ->
                        val cellSize = size.width.toFloat() / state.boardSize
                        val col = (offset.x / cellSize).toInt()
                        val row = (offset.y / cellSize).toInt()
                        if (col in 0 until state.boardSize && row in 0 until state.boardSize) {
                            onCellTap(Position(row, col))
                        }
                    }
                },
    ) {
        CanvasBoard(
            state = state,
        )

        PieceLayout(
            state = state,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun PieceLayout(
    state: BoardRenderState,
    modifier: Modifier = Modifier,
) {
    val activeQueens = state.queens.toList()

    Layout(
        content = {
            activeQueens.forEach { position ->
                QueenPiece(
                    isInConflict = state.isConflicting(position),
                    isSelected = state.isSelected(position),
                )
            }
        },
    ) { measurables, constraints ->
        val boardSize = state.boardSize
        val cellSize = constraints.maxWidth.toFloat() / boardSize
        val pieceConstraints =
            Constraints(
                minWidth = 0,
                maxWidth = ceil(cellSize).toInt(),
                minHeight = 0,
                maxHeight = ceil(cellSize).toInt(),
            )
        val placeables = measurables.map { it.measure(pieceConstraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val position = activeQueens[index]
                val row = position.row
                val col = position.col

                // Center the piece within the cell using float positioning
                val xPos = col * cellSize + (cellSize - placeable.width) / 2f
                val yPos = row * cellSize + (cellSize - placeable.height) / 2f

                placeable.place(
                    x = xPos.roundToInt(),
                    y = yPos.roundToInt(),
                )
            }
        }
    }
}

@Composable
private fun QueenPiece(
    isInConflict: Boolean,
    modifier: Modifier = Modifier,
    isSelected: Boolean? = null,
) {
    Icon(
        painterResource(R.drawable.queen),
        contentDescription = "queen",
        tint = if (isInConflict && isSelected == true) Color.White else QueenColor,
        modifier = modifier.fillMaxSize(0.6f),
    )
}

@Composable
private fun CanvasBoard(
    state: BoardRenderState,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier =
            modifier
                .aspectRatio(1f)
                .drawWithCache {
                    val cellSize = size.width / state.boardSize

                    onDrawBehind {
                        drawRect(LightSquareColor)

                        for (row in 0 until state.boardSize) {
                            for (col in 0 until state.boardSize) {
                                val position = Position(row, col)
                                val left = col * cellSize
                                val top = row * cellSize
                                val center = Offset(left + cellSize / 2, top + cellSize / 2)

                                // Check logic
                                val isDarkSquare = (row + col) % 2 == 1
                                val hasQueen = state.isQueen(position)
                                val isSelected = state.isSelected(position)
                                val isConflicting = state.isConflicting(position)
                                val isAttacked = state.isAttacked(position)

                                // 1. Draw Board Background (if dark square)
                                if (isDarkSquare) {
                                    drawRect(
                                        color = DarkSquareColor,
                                        topLeft = Offset(left, top),
                                        size = Size(cellSize, cellSize),
                                    )
                                }

                                // 2. Draw Conflicts or 3. Draw Hints
                                if (hasQueen) {
                                    if (isConflicting) {
                                        if (isSelected) {
                                            // Selected + Conflict: Red Background
                                            drawRect(
                                                color = ConflictColor,
                                                topLeft = Offset(left, top),
                                                size = Size(cellSize, cellSize),
                                            )
                                        } else {
                                            // Unselected + Conflict: Circle Outline
                                            val strokeWidth = cellSize * 0.1f
                                            val radius = (cellSize - strokeWidth) / 2
                                            drawCircle(
                                                color = attackedQueenColor,
                                                radius = radius * 0.87f,
                                                center = center,
                                                style = Stroke(width = strokeWidth),
                                            )
                                        }
                                    }
                                } else if (isAttacked) {
                                    // No Queen + Attacked: Small Hint Dot
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
                },
    )
}
