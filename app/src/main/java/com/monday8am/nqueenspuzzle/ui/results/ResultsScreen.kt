package com.monday8am.nqueenspuzzle.ui.results

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monday8am.nqueenspuzzle.data.ScoreEntry
import com.monday8am.nqueenspuzzle.ui.theme.NQueensPuzzleTheme

@Composable
fun ResultsScreen(
    viewModel: ResultsViewModel,
    onNewGameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scores by viewModel.scores.collectAsStateWithLifecycle()

    ResultsScreenContent(
        boardSize = viewModel.boardSize,
        elapsedSeconds = viewModel.elapsedSeconds,
        previousScores = scores,
        onNewGameClick = onNewGameClick,
        modifier = modifier,
    )
}

@Composable
private fun ResultsScreenContent(
    boardSize: Int,
    elapsedSeconds: Long,
    previousScores: List<ScoreEntry>,
    onNewGameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val contentPadding = PaddingValues(24.dp)

        if (isLandscape) {
            LandscapeLayout(
                boardSize = boardSize,
                elapsedSeconds = elapsedSeconds,
                previousScores = previousScores,
                onNewGameClick = onNewGameClick,
                contentPadding = contentPadding,
            )
        } else {
            PortraitLayout(
                boardSize = boardSize,
                elapsedSeconds = elapsedSeconds,
                previousScores = previousScores,
                onNewGameClick = onNewGameClick,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun PortraitLayout(
    boardSize: Int,
    elapsedSeconds: Long,
    previousScores: List<ScoreEntry>,
    onNewGameClick: () -> Unit,
    contentPadding: PaddingValues, // Receive padding
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
        // Apply padding here
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        CompletionCard(
            boardSize = boardSize,
            elapsedSeconds = elapsedSeconds,
        )

        Box(modifier = Modifier.weight(1f)) {
            ScoreList(
                boardSize = boardSize,
                previousScores = previousScores,
            )
        }

        NewGameButton(
            onNewGameClick = onNewGameClick,
        )
    }
}

@Composable
private fun LandscapeLayout(
    boardSize: Int,
    elapsedSeconds: Long,
    previousScores: List<ScoreEntry>,
    onNewGameClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically, // Center content vertically
    ) {
        CompletionCard(
            boardSize = boardSize,
            elapsedSeconds = elapsedSeconds,
            alignment = Alignment.Start,
            modifier =
                Modifier
                    .weight(1f),
            // Removed fillMaxHeight so the card is only as tall as it needs to be
        )

        // Right block
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Same weight optimization for Landscape
            Box(modifier = Modifier.weight(1f)) {
                ScoreList(
                    boardSize = boardSize,
                    previousScores = previousScores,
                )
            }

            NewGameButton(
                onNewGameClick = onNewGameClick,
            )
        }
    }
}

@Composable
private fun CompletionCard(
    boardSize: Int,
    elapsedSeconds: Long,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = alignment,
        modifier = modifier,
    ) {
        Text(
            text = "Puzzle Solved!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Board Size: ${boardSize}x$boardSize",
                    style = MaterialTheme.typography.titleLarge,
                )
                // Assuming formatTime is available globally or imported
                Text(
                    text = "Time: ${formatTime(elapsedSeconds)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun NewGameButton(
    onNewGameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // New game button
    Button(
        onClick = onNewGameClick,
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp),
    ) {
        Text(
            text = "New Game",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview(showBackground = true, name = "Portrait Preview")
@Composable
private fun ResultsScreenPreview() {
    NQueensPuzzleTheme {
        ResultsScreenContent(
            boardSize = 8,
            elapsedSeconds = 125,
            previousScores =
                listOf(
                    ScoreEntry(1, 8, 100, System.currentTimeMillis()),
                    ScoreEntry(2, 8, 145, System.currentTimeMillis() - 86400000),
                    ScoreEntry(3, 8, 200, System.currentTimeMillis() - 172800000),
                ),
            onNewGameClick = {},
        )
    }
}

@Preview(
    showBackground = true,
    name = "Landscape Preview",
    device = "spec:width=800dp,height=360dp,orientation=landscape",
)
@Composable
private fun ResultsScreenLandscapePreview() {
    NQueensPuzzleTheme {
        ResultsScreenContent(
            boardSize = 8,
            elapsedSeconds = 125,
            previousScores =
                listOf(
                    ScoreEntry(1, 8, 100, System.currentTimeMillis()),
                    ScoreEntry(2, 8, 145, System.currentTimeMillis() - 86400000),
                    ScoreEntry(3, 8, 200, System.currentTimeMillis() - 172800000),
                ),
            onNewGameClick = {},
        )
    }
}
