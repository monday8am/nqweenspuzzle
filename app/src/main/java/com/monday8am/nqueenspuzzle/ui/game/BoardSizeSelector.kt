package com.monday8am.nqueenspuzzle.ui.game

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.monday8am.nqueenspuzzle.logic.models.BOARD_MAX_SIZE
import com.monday8am.nqueenspuzzle.logic.models.BOARD_MIN_SIZE
import kotlin.math.roundToInt

@Composable
internal fun BoardSizeSelector(
    boardSize: Int,
    onBoardSizeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Board size selector
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        val boardSizeLabel = "Board size: ${boardSize}x$boardSize"
        Text(boardSizeLabel)
        Slider(
            value = boardSize.toFloat(),
            onValueChange = { onBoardSizeSelected(it.roundToInt()) },
            valueRange = BOARD_MIN_SIZE.toFloat()..BOARD_MAX_SIZE.toFloat(),
            steps = (BOARD_MAX_SIZE - BOARD_MIN_SIZE) - 1,
        )
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
private fun BoardSizeSelectorPreview() {
    BoardSizeSelector(
        boardSize = 8,
        onBoardSizeSelected = {},
    )
}
