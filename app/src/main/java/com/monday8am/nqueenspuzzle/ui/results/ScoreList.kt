package com.monday8am.nqueenspuzzle.ui.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.monday8am.nqueenspuzzle.data.ScoreEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun ScoreList(
    boardSize: Int,
    previousScores: List<ScoreEntry>,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = CenterHorizontally,
) {
    Column(
        verticalArrangement = spacedBy(16.dp),
        horizontalAlignment = alignment,
        modifier = modifier,
    ) {
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
                verticalArrangement = spacedBy(8.dp),
            ) {
                items(previousScores.take(10)) { score ->
                    ScoreItem(score = score)
                }
            }
        } else {
            Spacer(modifier = modifier.weight(1f))
        }
    }
}

@Composable
internal fun ScoreItem(
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

internal fun formatTime(seconds: Long): String =
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
