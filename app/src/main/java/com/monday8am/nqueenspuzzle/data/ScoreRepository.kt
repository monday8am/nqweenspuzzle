package com.monday8am.nqueenspuzzle.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
     * Add a new score entry
     * PLACEHOLDER - Implementation deferred
     */
    suspend fun addScore(boardSize: Int, elapsedSeconds: Long) {
        // TODO: Implement in next phase
        // 1. Get next ID
        // 2. Create ScoreEntry with current timestamp
        // 3. Read existing scores for board size
        // 4. Append new score
        // 5. Serialize to JSON
        // 6. Write back to DataStore
    }

    /**
     * Delete a specific score entry
     * PLACEHOLDER - For future use
     */
    suspend fun deleteScore(boardSize: Int, scoreId: Int) {
        // TODO: Implement if needed
    }

    /**
     * Clear all scores for a board size
     * PLACEHOLDER - For future use
     */
    suspend fun clearScoresForBoardSize(boardSize: Int) {
        // TODO: Implement if needed
    }
}
