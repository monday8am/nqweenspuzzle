package com.monday8am.nqueenspuzzle

import com.monday8am.nqueenspuzzle.models.Difficulty
import com.monday8am.nqueenspuzzle.models.Position

sealed class UserAction {
    data class TapCell(
        val position: Position,
    ) : UserAction()

    data class SetBoardSize(
        val size: Int,
    ) : UserAction()

    data class SetDifficulty(
        val difficulty: Difficulty,
    ) : UserAction()

    data object Reset : UserAction()
}
