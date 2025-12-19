package com.monday8am.nqueenspuzzle.game

import com.monday8am.nqueenspuzzle.models.Difficulty

data class GameConfig(
    val boardSize: Int = 8,
    val difficulty: Difficulty = Difficulty.EASY,
) {
    init {
        require(boardSize in 4..12) { "Board size must be between 4 and 12" }
    }
}
