package com.monday8am.nqueenspuzzle.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.monday8am.nqueenspuzzle.GameAction
import com.monday8am.nqueenspuzzle.GameViewModel
import com.monday8am.nqueenspuzzle.data.ScoreRepository
import com.monday8am.nqueenspuzzle.ui.GameScreen
import com.monday8am.nqueenspuzzle.ui.ResultsScreen

@Composable
fun NQueensNavHost(
    viewModel: GameViewModel,
    scoreRepository: ScoreRepository,
    navController: NavHostController = rememberNavController()
) {
    // Observe navigation events from ViewModel
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToResults -> {
                    navController.navigate(
                        NavigationRoutes.resultsScreen(
                            boardSize = event.boardSize,
                            elapsedSeconds = event.elapsedSeconds
                        )
                    )
                }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.GAME_SCREEN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationRoutes.GAME_SCREEN) {
                GameScreen(viewModel = viewModel)
            }

            composable(
                route = NavigationRoutes.RESULTS_SCREEN,
                arguments = listOf(
                    navArgument("boardSize") { type = NavType.IntType },
                    navArgument("elapsedSeconds") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val boardSize = backStackEntry.arguments?.getInt("boardSize") ?: 8
                val elapsedSeconds = backStackEntry.arguments?.getLong("elapsedSeconds") ?: 0L

                ResultsScreen(
                    boardSize = boardSize,
                    elapsedSeconds = elapsedSeconds,
                    scoreRepository = scoreRepository,
                    onNewGameClick = {
                        viewModel.dispatch(GameAction.Reset)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
