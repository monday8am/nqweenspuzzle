package com.monday8am.nqueenspuzzle.game

import com.monday8am.nqueenspuzzle.models.BoardRenderState


sealed class GameState {
    data class Board(val renderState: BoardRenderState) : GameState()
    data class UserWon(
        val gameTimeMillis: Long,
        val renderState: BoardRenderState,
    ) : GameState()
}
