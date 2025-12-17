package com.monday8am.nqueenspuzzle.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Extension property for DataStore
private val Context.scoresDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "n_queens_scores"
)

class ScoreRepository(context: Context) {

    private val dataStore = context.scoresDataStore

    fun getScoresForBoardSize(boardSize: Int): Flow<List<ScoreEntry>> {
        return dataStore.data.map { preferences ->
            val key = stringPreferencesKey("scores_boardSize_$boardSize")
            val jsonString = preferences[key] ?: "[]"
            try {
                val scores = Json.decodeFromString<List<ScoreEntry>>(jsonString)
                scores.sortedBy { it.elapsedSeconds }  // Fastest times first
            } catch (_: Exception) {
                emptyList()  // Return empty list if parsing fails
            }
        }
    }

    /**
     * Add a new score entry with FIFO strategy
     * - Keeps max 10 records per board size
     * - If there are more than 10, removes oldest ones (FIFO)
     */
    suspend fun addScore(boardSize: Int, elapsedSeconds: Long) {
        dataStore.edit { preferences ->
            val key = stringPreferencesKey("scores_boardSize_$boardSize")
            val nextIdKey = intPreferencesKey("next_score_id")

            // Get next ID
            val nextId = preferences[nextIdKey] ?: 1
            preferences[nextIdKey] = nextId + 1

            // Read existing scores
            val jsonString = preferences[key] ?: "[]"
            val existingScores = try {
                Json.decodeFromString<List<ScoreEntry>>(jsonString)
            } catch (_: Exception) {
                emptyList()
            }

            // Create new score entry
            val newScore = ScoreEntry(
                id = nextId,
                boardSize = boardSize,
                elapsedSeconds = elapsedSeconds,
                timestamp = System.currentTimeMillis()
            )

            // Add new score and apply FIFO strategy
            val allScores = existingScores + newScore

            // Sort by timestamp (oldest first)
            val sortedByTime = allScores.sortedBy { it.timestamp }
            val finalScores = when {
                sortedByTime.size > 10 -> sortedByTime.takeLast(10)
                else -> sortedByTime
            }

            // Save back to DataStore
            val updatedJson = Json.encodeToString(finalScores)
            preferences[key] = updatedJson
        }
    }

    suspend fun deleteScore(boardSize: Int, scoreId: Int) {
        // TODO: Implement if needed
    }

    suspend fun clearScoresForBoardSize(boardSize: Int) {
        // TODO: Implement if needed
    }
}
