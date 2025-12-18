package com.monday8am.nqueenspuzzle.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

private const val BOARD_MIN_SIZE = 4f
private const val BOARD_MAX_SIZE = 12f

@Composable
internal fun BoardSizeSelector(
    boardSize: Int,
    onBoardSizeSelected: (Int) -> Unit,
) {
    // Board size selector
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val boardSizeLabel = "Board size: ${boardSize}x${boardSize}"
        Text(boardSizeLabel)
        Slider(
            value = boardSize.toFloat(),
            onValueChange = { onBoardSizeSelected(it.roundToInt()) },
            valueRange = BOARD_MIN_SIZE..BOARD_MAX_SIZE,
            steps = (BOARD_MAX_SIZE - BOARD_MIN_SIZE).toInt() - 1,
        )
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
private fun BoardSizeSelectorPreview() {
    BoardSizeSelector(
        boardSize = 8,
        onBoardSizeSelected = {}
    )
}
