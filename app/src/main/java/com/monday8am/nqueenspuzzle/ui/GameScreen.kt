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
import com.monday8am.nqueenspuzzle.GameAction
import com.monday8am.nqueenspuzzle.GameViewModel
import com.monday8am.nqueenspuzzle.logic.NQueensLogic
import com.monday8am.nqueenspuzzle.models.BoardRenderState
import com.monday8am.nqueenspuzzle.models.Position

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
        onShowHint = { viewModel.dispatch(GameAction.ShowHint) },
        modifier = modifier,
    )
}

@Composable
private fun GameScreenContent(
    state: BoardRenderState,
    onBoardSizeSelected: (Int) -> Unit,
    onCellTap: (Position) -> Unit,
    onResetClick: () -> Unit,
    onShowHint: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LandscapeLayout(
            state = state,
            onBoardSizeSelected = onBoardSizeSelected,
            onShowHint = onShowHint,
            onCellTap = onCellTap,
            onResetClick = onResetClick,
            modifier = modifier
        )
    } else {
        PortraitLayout(
            state = state,
            onBoardSizeSelected = onBoardSizeSelected,
            onCellTap = onCellTap,
            onResetClick = onResetClick,
            onShowHint = onShowHint,
            modifier = modifier
        )
    }
}

@Composable
private fun PortraitLayout(
    state: BoardRenderState,
    onBoardSizeSelected: (Int) -> Unit,
    onCellTap: (Position) -> Unit,
    onShowHint: () -> Unit,
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

        GameBoard(
            state = state,
            onCellTap = onCellTap,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        )

        ControlPanel(
            boardSize = state.boardSize,
            isSolved = state.isSolved,
            onBoardSizeSelected = onBoardSizeSelected,
            onShowHint = onShowHint,
            onResetClick = onResetClick,
        )
    }
}

@Composable
private fun LandscapeLayout(
    state: BoardRenderState,
    onBoardSizeSelected: (Int) -> Unit,
    onCellTap: (Position) -> Unit,
    onResetClick: () -> Unit,
    onShowHint: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "N-Queens Puzzle",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Row(
            horizontalArrangement = spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Left section: GameBoard
            GameBoard(
                state = state,
                onCellTap = onCellTap,
                modifier = Modifier.fillMaxHeight()
            )

            // Right section: Controls
            ControlPanel(
                boardSize = state.boardSize,
                isSolved = state.isSolved,
                onBoardSizeSelected = onBoardSizeSelected,
                onResetClick = onResetClick,
                onShowHint = onShowHint,
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true, name = "Portrait Preview")
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
        onShowHint = { },
        onResetClick = { },
    )
}

@Preview(
    showBackground = true,
    name = "Landscape Preview",
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
private fun GameScreenContentLandscapePreview() {
    GameScreenContent(
        state = NQueensLogic.buildBoardRenderState(
            boardSize = 8,
            queens = setOf(Position(0, 0), Position(1, 2), Position(0, 4)),
            selectedQueen = Position(0, 0),
        ),
        onBoardSizeSelected = { },
        onCellTap = { },
        onShowHint = { },
        onResetClick = { },
    )
}
