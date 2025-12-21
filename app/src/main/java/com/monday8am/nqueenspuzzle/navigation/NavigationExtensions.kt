package com.monday8am.nqueenspuzzle.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.monday8am.nqueenspuzzle.data.ScoreRepository
import com.monday8am.nqueenspuzzle.ui.game.GameScreen
import com.monday8am.nqueenspuzzle.ui.game.GameViewModel
import com.monday8am.nqueenspuzzle.ui.game.UserAction
import com.monday8am.nqueenspuzzle.ui.results.ResultsScreen
import com.monday8am.nqueenspuzzle.ui.results.ResultsViewModel

fun NavGraphBuilder.gameScreen(viewModel: GameViewModel) {
    composable<GameRoute> {
        GameScreen(viewModel = viewModel)
    }
}

fun NavGraphBuilder.resultsScreen(
    scoreRepository: ScoreRepository,
    navController: NavController,
    gameViewModel: GameViewModel,
) {
    composable<ResultsRoute> { backStackEntry ->
        val resultsRoute = backStackEntry.toRoute<ResultsRoute>()

        val resultsViewModel: ResultsViewModel =
            viewModel(
                factory =
                    ResultsViewModel.provideFactory(
                        scoreRepository = scoreRepository,
                        boardSize = resultsRoute.boardSize,
                        elapsedSeconds = resultsRoute.elapsedSeconds,
                    ),
            )

        ResultsScreen(
            viewModel = resultsViewModel,
            onNewGameClick = {
                gameViewModel.dispatch(UserAction.Reset)
                navController.popBackStack()
            },
        )
    }
}
