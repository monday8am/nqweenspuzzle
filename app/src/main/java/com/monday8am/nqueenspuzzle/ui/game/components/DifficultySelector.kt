package com.monday8am.nqueenspuzzle.ui.game.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.monday8am.nqueenspuzzle.logic.models.Difficulty
import kotlin.math.roundToInt

@Composable
internal fun DifficultySelector(
    difficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text("Difficulty: ${difficulty.getDisplayName()}")
        Slider(
            value = difficulty.toFloat(),
            onValueChange = { value ->
                onDifficultySelected(value.roundToInt().toDifficulty())
            },
            valueRange = 0f..2f,
            steps = 1,
        )
    }
}

private fun Difficulty.toFloat(): Float =
    when (this) {
        Difficulty.EASY -> 0f
        Difficulty.MEDIUM -> 1f
        Difficulty.HARD -> 2f
    }

private fun Int.toDifficulty(): Difficulty =
    when (this) {
        0 -> Difficulty.EASY
        1 -> Difficulty.MEDIUM
        2 -> Difficulty.HARD
        else -> Difficulty.EASY
    }

@Preview(showBackground = true, widthDp = 300)
@Composable
private fun DifficultySelectorPreview() {
    DifficultySelector(
        difficulty = Difficulty.MEDIUM,
        onDifficultySelected = {},
    )
}
