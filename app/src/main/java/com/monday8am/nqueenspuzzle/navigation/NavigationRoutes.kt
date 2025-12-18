package com.monday8am.nqueenspuzzle.navigation

import kotlinx.serialization.Serializable

@Serializable
object GameRoute

@Serializable
data class ResultsRoute(
    val boardSize: Int,
    val elapsedSeconds: Long,
)
