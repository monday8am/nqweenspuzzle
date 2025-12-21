package com.monday8am.nqueenspuzzle.logic.models

import com.monday8am.nqueenspuzzle.logic.BOARD_MAX_SIZE
import com.monday8am.nqueenspuzzle.logic.BOARD_MIN_SIZE

data class GameConfig(
    val boardSize: Int = 8,
    val difficulty: Difficulty = Difficulty.EASY,
) {
    init {
        require(boardSize in BOARD_MIN_SIZE..BOARD_MAX_SIZE) { "Board size must be between ${BOARD_MIN_SIZE} and ${BOARD_MAX_SIZE}" }
    }
}
