package com.monday8am.nqueenspuzzle.data

import kotlinx.serialization.Serializable

@Serializable
data class ScoreEntry(
    val id: Int,
    val boardSize: Int,
    val elapsedSeconds: Long,
    val timestamp: Long,
)
