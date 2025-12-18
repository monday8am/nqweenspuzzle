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

private const val LEADERBOARD_SIZE = 10
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

    /* Leaderboard filtered by board size and sorted by fastest time.
     *  Only the top 10 scores are returned.
     */
    suspend fun addScore(boardSize: Int, elapsedSeconds: Long) {
        dataStore.edit { preferences ->
            val key = stringPreferencesKey("scores_boardSize_$boardSize")
            val nextIdKey = intPreferencesKey("next_score_id")

            val nextId = preferences[nextIdKey] ?: 1
            preferences[nextIdKey] = nextId + 1

            val jsonString = preferences[key] ?: "[]"
            val existingScores = try {
                Json.decodeFromString<List<ScoreEntry>>(jsonString)
            } catch (_: Exception) {
                emptyList()
            }

            val newScore = ScoreEntry(
                id = nextId,
                boardSize = boardSize,
                elapsedSeconds = elapsedSeconds,
                timestamp = System.currentTimeMillis()
            )

            val allScores = existingScores + newScore
            val sortedBySpeed = allScores.sortedBy { it.elapsedSeconds }
            val finalScores = sortedBySpeed.take(LEADERBOARD_SIZE)

            preferences[key] = Json.encodeToString(finalScores)
        }
    }
}
