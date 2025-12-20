package com.monday8am.nqueenspuzzle.ui.game

import com.monday8am.nqueenspuzzle.audio.SoundEffect
import com.monday8am.nqueenspuzzle.navigation.ResultsRoute

sealed interface GameSideEffect {
    data class PlaySound(
        val effect: SoundEffect,
    ) : GameSideEffect

    data class NavigateToResults(
        val route: ResultsRoute,
    ) : GameSideEffect
}
