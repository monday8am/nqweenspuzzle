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
import com.monday8am.nqueenspuzzle.GameAction
import com.monday8am.nqueenspuzzle.GameViewModel
import com.monday8am.nqueenspuzzle.data.ScoreRepository
import com.monday8am.nqueenspuzzle.ui.GameScreen
import com.monday8am.nqueenspuzzle.ui.ResultsScreen
import com.monday8am.nqueenspuzzle.ui.ResultsViewModel

@Composable
fun NQueensNavHost(
    viewModel: GameViewModel,
    scoreRepository: ScoreRepository,
    navController: NavHostController = rememberNavController()
) {
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToResults -> {
                    navController.navigate(event.route)
                }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = GameRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<GameRoute> {
                GameScreen(viewModel = viewModel)
            }

            composable<ResultsRoute> { backStackEntry ->
                val resultsRoute = backStackEntry.toRoute<ResultsRoute>()

                // Create ViewModel factory with parameters
                val resultsViewModel: ResultsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ResultsViewModel(
                                scoreRepository = scoreRepository,
                                boardSize = resultsRoute.boardSize,
                                elapsedSeconds = resultsRoute.elapsedSeconds
                            ) as T
                        }
                    }
                )

                ResultsScreen(
                    viewModel = resultsViewModel,
                    onNewGameClick = {
                        viewModel.dispatch(GameAction.Reset)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
