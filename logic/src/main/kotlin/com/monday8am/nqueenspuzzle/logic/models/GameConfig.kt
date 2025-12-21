package com.monday8am.nqueenspuzzle.logic.models

const val BOARD_MIN_SIZE = 4
const val BOARD_MAX_SIZE = 12

data class GameConfig(
    val boardSize: Int = 8,
    val difficulty: Difficulty = Difficulty.EASY,
) {
    init {
        require(boardSize in BOARD_MIN_SIZE..BOARD_MAX_SIZE) { "Board size must be between $BOARD_MIN_SIZE and $BOARD_MAX_SIZE" }
    }
}
