package com.monday8am.nqueenspuzzle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.monday8am.nqueenspuzzle.models.Difficulty

@Composable
internal fun ControlPanel(
    boardSize: Int,
    difficulty: Difficulty,
    isSolved: Boolean,
    onBoardSizeSelected: (Int) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onResetClick: () -> Unit,
    verticalArrangement: Arrangement.Vertical = spacedBy(16.dp),
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = Alignment.Start,
        modifier = modifier,
    ) {
        if (isSolved) {
            Text(
                text = "Congratulations! Puzzle Solved!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        BoardSizeSelector(
            boardSize = boardSize,
            onBoardSizeSelected = onBoardSizeSelected,
        )

        DifficultySelector(
            difficulty = difficulty,
            onDifficultySelected = onDifficultySelected,
        )

        Row(
            horizontalArrangement = spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = onResetClick,
                modifier = Modifier.width(120.dp),
            ) {
                Text("Reset")
            }
            Button(
                onClick = onResetClick,
                modifier = Modifier.width(120.dp),
            ) {
                Text("Theme")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ControlPanelPreview() {
    MaterialTheme {
        ControlPanel(
            boardSize = 8,
            difficulty = Difficulty.EASY,
            isSolved = false,
            onBoardSizeSelected = {},
            onDifficultySelected = {},
            onResetClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ControlPanelSolvedPreview() {
    MaterialTheme {
        ControlPanel(
            boardSize = 8,
            difficulty = Difficulty.EASY,
            isSolved = true,
            onBoardSizeSelected = {},
            onDifficultySelected = {},
            onResetClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
