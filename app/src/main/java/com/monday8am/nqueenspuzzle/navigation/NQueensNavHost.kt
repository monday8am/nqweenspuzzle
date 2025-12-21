package com.monday8am.nqueenspuzzle.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.monday8am.nqueenspuzzle.audio.SoundEffectManager
import com.monday8am.nqueenspuzzle.data.ScoreRepository
import com.monday8am.nqueenspuzzle.ui.game.GameSideEffect
import com.monday8am.nqueenspuzzle.ui.game.GameViewModel

@Composable
fun NQueensNavHost(
    viewModel: GameViewModel,
    scoreRepository: ScoreRepository,
    soundEffectManager: SoundEffectManager,
    navController: NavHostController = rememberNavController(),
) {
    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is GameSideEffect.NavigateToResults -> {
                    navController.navigate(effect.route)
                }

                is GameSideEffect.PlaySound -> {
                    soundEffectManager.play(effect.effect)
                }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = GameRoute,
            modifier = Modifier.padding(innerPadding),
        ) {
            gameScreen(viewModel = viewModel)
            resultsScreen(
                scoreRepository = scoreRepository,
                navController = navController,
                gameViewModel = viewModel,
            )
        }
    }
}
