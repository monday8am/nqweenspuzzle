package com.monday8am.nqueenspuzzle.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.monday8am.nqueenspuzzle.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.renderState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "N-Queens Puzzle",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Board size selector
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(4, 5, 6, 7, 8).forEach { size ->
                FilterChip(
                    selected = state.boardSize == size,
                    onClick = { viewModel.setBoardSize(size) },
                    label = { Text("$size x $size") },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Queens remaining
        Text(
            text = "Queens remaining: ${state.queensRemaining}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Game board
        GameBoard(
            state = state,
            onCellTap = { position -> viewModel.onCellTap(position) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            onClick = { viewModel.reset() },
            modifier = Modifier.width(120.dp)
        ) {
            Text("Reset")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
