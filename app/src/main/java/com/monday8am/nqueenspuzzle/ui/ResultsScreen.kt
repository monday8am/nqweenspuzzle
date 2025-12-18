package com.monday8am.nqueenspuzzle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.monday8am.nqueenspuzzle.data.ScoreEntry
import com.monday8am.nqueenspuzzle.ui.theme.NQueensPuzzleTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultsScreen(
    viewModel: ResultsViewModel,
    onNewGameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scores by viewModel.scores.collectAsState()

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
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Title
        Text(
            text = "Puzzle Solved!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        // Current completion card
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
                Text(
                    text = "Time: ${formatTime(elapsedSeconds)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Previous scores section
        if (previousScores.isNotEmpty()) {
            Text(
                text = "Previous Times (${boardSize}x$boardSize)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(previousScores.take(10)) { score ->
                    ScoreItem(score = score)
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // New game button
        Button(
            onClick = onNewGameClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
        ) {
            Text(
                text = "New Game",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun ScoreItem(
    score: ScoreEntry,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = formatTime(score.elapsedSeconds),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formatDate(score.timestamp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatTime(seconds: Long): String =
    if (seconds < 60) {
        "$seconds seconds"
    } else {
        val minutes = seconds / 60
        val secs = seconds % 60
        "$minutes:${secs.toString().padStart(2, '0')}"
    }

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

@Preview(showBackground = true)
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
