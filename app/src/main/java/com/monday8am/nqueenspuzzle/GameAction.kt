package com.monday8am.nqueenspuzzle

import com.monday8am.nqueenspuzzle.models.Difficulty
import com.monday8am.nqueenspuzzle.models.Position

sealed class GameAction {
    data class TapCell(
        val position: Position,
    ) : GameAction()

    data class SetBoardSize(
        val size: Int,
    ) : GameAction()

    data class SetDifficulty(
        val difficulty: Difficulty,
    ) : GameAction()

    data object Reset : GameAction()
}
