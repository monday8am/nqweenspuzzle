package com.monday8am.nqueenspuzzle.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.monday8am.nqueenspuzzle.GameViewModel
import com.monday8am.nqueenspuzzle.UserAction
import com.monday8am.nqueenspuzzle.models.Difficulty
import com.monday8am.nqueenspuzzle.models.Position

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.renderState.collectAsState()

    GameScreenContent(
        state = state,
        onBoardSizeSelected = { size -> viewModel.dispatch(UserAction.SetBoardSize(size)) },
        onDifficultySelected = { difficulty -> viewModel.dispatch(UserAction.SetDifficulty(difficulty)) },
        onCellTap = { position -> viewModel.dispatch(UserAction.TapCell(position)) },
        onResetClick = { viewModel.dispatch(UserAction.Reset) },
        modifier = modifier,
    )
}

@Composable
private fun GameScreenContent(
    state: BoardRenderState,
    onBoardSizeSelected: (Int) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onCellTap: (Position) -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LandscapeLayout(
            state = state,
            onBoardSizeSelected = onBoardSizeSelected,
            onDifficultySelected = onDifficultySelected,
            onCellTap = onCellTap,
            onResetClick = onResetClick,
            modifier = modifier,
        )
    } else {
        PortraitLayout(
            state = state,
            onBoardSizeSelected = onBoardSizeSelected,
            onDifficultySelected = onDifficultySelected,
            onCellTap = onCellTap,
            onResetClick = onResetClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun PortraitLayout(
    state: BoardRenderState,
    onBoardSizeSelected: (Int) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onCellTap: (Position) -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Text(
            text = "N-Queens Puzzle",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        GameBoard(
            state = state,
            onCellTap = onCellTap,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
        )

        ControlPanel(
            boardSize = state.boardSize,
            difficulty = state.difficulty,
            isSolved = state.isSolved,
            onBoardSizeSelected = onBoardSizeSelected,
            onDifficultySelected = onDifficultySelected,
            onResetClick = onResetClick,
        )
    }
}

@Composable
private fun LandscapeLayout(
    state: BoardRenderState,
    onBoardSizeSelected: (Int) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onCellTap: (Position) -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = spacedBy(12.dp),
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Text(
            text = "N-Queens Puzzle",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start),
        )

        Row(
            horizontalArrangement = spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            // Left section: GameBoard
            GameBoard(
                state = state,
                onCellTap = onCellTap,
                modifier = Modifier.fillMaxHeight(),
            )

            // Right section: Controls
            ControlPanel(
                boardSize = state.boardSize,
                difficulty = state.difficulty,
                isSolved = state.isSolved,
                onBoardSizeSelected = onBoardSizeSelected,
                onDifficultySelected = onDifficultySelected,
                onResetClick = onResetClick,
                modifier = Modifier,
            )
        }
    }
}

@Preview(showBackground = true, name = "Portrait Preview")
@Composable
private fun GameScreenContentPreview() {
    GameScreenContent(
        state = sampleBoardRenderState,
        onBoardSizeSelected = { },
        onDifficultySelected = { },
        onCellTap = { },
        onResetClick = { },
    )
}

@Preview(
    showBackground = true,
    name = "Landscape Preview",
    device = "spec:width=800dp,height=360dp,orientation=landscape",
)
@Composable
private fun GameScreenContentLandscapePreview() {
    GameScreenContent(
        state = sampleBoardRenderState,
        onBoardSizeSelected = { },
        onDifficultySelected = { },
        onCellTap = { },
        onResetClick = { },
    )
}
