package com.monday8am.nqueenspuzzle.navigation

object NavigationRoutes {
    const val GAME_SCREEN = "game"
    const val RESULTS_SCREEN = "results/{boardSize}/{elapsedSeconds}"

    fun resultsScreen(boardSize: Int, elapsedSeconds: Long): String {
        return "results/$boardSize/$elapsedSeconds"
    }
}
