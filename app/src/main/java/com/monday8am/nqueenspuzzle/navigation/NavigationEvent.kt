package com.monday8am.nqueenspuzzle.navigation

sealed class NavigationEvent {
    data class NavigateToResults(
        val boardSize: Int,
        val elapsedSeconds: Long
    ) : NavigationEvent()
}
