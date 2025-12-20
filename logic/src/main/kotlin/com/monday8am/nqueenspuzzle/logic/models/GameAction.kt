package com.monday8am.nqueenspuzzle.logic.models

sealed class GameAction {
    data class QueenAdded(
        val position: Position,
        val causedConflict: Boolean,
    ) : GameAction()

    data class QueenRemoved(
        val position: Position,
    ) : GameAction()

    data class QueenMoved(
        val from: Position,
        val to: Position,
        val causedConflict: Boolean,
    ) : GameAction()

    data object GameWon : GameAction()

    data object GameReset : GameAction()

    fun updateConflict(value: Boolean): GameAction {
        return when (this) {
            is QueenAdded -> copy(causedConflict = value)
            is QueenMoved -> copy(causedConflict = value)
            else -> this
        }
    }

    fun causedConflict(): Boolean {
        return when (this) {
            is QueenAdded -> causedConflict
            is QueenMoved -> causedConflict
            else -> false
        }
    }
}
