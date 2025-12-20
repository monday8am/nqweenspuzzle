package com.monday8am.nqueenspuzzle.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.monday8am.nqueenspuzzle.audio.SoundEffectManager
import com.monday8am.nqueenspuzzle.data.ScoreRepository
import com.monday8am.nqueenspuzzle.ui.game.GameScreen
import com.monday8am.nqueenspuzzle.ui.game.GameSideEffect
import com.monday8am.nqueenspuzzle.ui.game.GameViewModel
import com.monday8am.nqueenspuzzle.ui.game.UserAction
import com.monday8am.nqueenspuzzle.ui.results.ResultsScreen
import com.monday8am.nqueenspuzzle.ui.results.ResultsViewModel

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
            composable<GameRoute> {
                GameScreen(viewModel = viewModel)
            }

            composable<ResultsRoute> { backStackEntry ->
                val resultsRoute = backStackEntry.toRoute<ResultsRoute>()

                // Create ViewModel factory with parameters
                val resultsViewModel: ResultsViewModel =
                    viewModel(
                        factory =
                            object : ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                    ResultsViewModel(
                                        scoreRepository = scoreRepository,
                                        boardSize = resultsRoute.boardSize,
                                        elapsedSeconds = resultsRoute.elapsedSeconds,
                                    ) as T
                            },
                    )

                ResultsScreen(
                    viewModel = resultsViewModel,
                    onNewGameClick = {
                        viewModel.dispatch(UserAction.Reset)
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}
