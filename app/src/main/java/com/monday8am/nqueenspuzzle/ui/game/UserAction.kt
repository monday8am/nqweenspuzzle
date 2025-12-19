package com.monday8am.nqueenspuzzle.ui.game

import com.monday8am.nqueenspuzzle.logic.models.Difficulty
import com.monday8am.nqueenspuzzle.logic.models.Position

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
