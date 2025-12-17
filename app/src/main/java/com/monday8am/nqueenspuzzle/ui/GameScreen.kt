package com.monday8am.nqueenspuzzle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.monday8am.nqueenspuzzle.GameAction
import com.monday8am.nqueenspuzzle.GameViewModel
import com.monday8am.nqueenspuzzle.logic.NQueensLogic
import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.Position
import kotlin.math.roundToInt

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.renderState.collectAsState()

    GameScreenContent(
        state = state,
        onBoardSizeSelected = { size -> viewModel.dispatch(GameAction.SetBoardSize(size)) },
        onCellTap = { position -> viewModel.dispatch(GameAction.TapCell(position)) },
        onResetClick = { viewModel.dispatch(GameAction.Reset) },
        modifier = modifier,
    )
}

@Composable
private fun GameScreenContent(
    state: BoardRenderState,
    onBoardSizeSelected: (Int) -> Unit,
    onCellTap: (Position) -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "N-Queens Puzzle",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Board size selector
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val boardSizeLabel = "Board size: ${state.boardSize}x${state.boardSize}"
            Text(boardSizeLabel)
            Slider(
                value = state.boardSize.toFloat(),
                onValueChange = { onBoardSizeSelected(it.roundToInt()) },
                valueRange = 4f..12f,
                steps = 7 // (16 - 4) - 1
            )
        }

        // Queens remaining
        Text(
            text = "Queens remaining: ${state.queensRemaining}",
            style = MaterialTheme.typography.titleMedium
        )

        // Game board
        GameBoard(
            state = state,
            onCellTap = onCellTap,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        )

        // Victory message
        if (state.isSolved) {
            Text(
                text = "Congratulations! Puzzle Solved!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Reset button
        Button(
            onClick = onResetClick,
            modifier = Modifier.width(120.dp)
        ) {
            Text("Reset")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenContentPreview() {
    GameScreenContent(
        state = NQueensLogic.buildBoardRenderState(
            boardSize = 8,
            queens = setOf(Position(0, 0), Position(1, 2), Position(0, 4)),
            selectedQueen = Position(0, 0),
        ),
        onBoardSizeSelected = { },
        onCellTap = { },
        onResetClick = { },
    )
}
