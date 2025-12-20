package com.monday8am.nqueenspuzzle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.monday8am.nqueenspuzzle.audio.SoundEffectManager
import com.monday8am.nqueenspuzzle.data.ScoreRepository
import com.monday8am.nqueenspuzzle.navigation.NQueensNavHost
import com.monday8am.nqueenspuzzle.ui.game.GameViewModel
import com.monday8am.nqueenspuzzle.ui.theme.NQueensPuzzleTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()
    private lateinit var scoreRepository: ScoreRepository
    private lateinit var soundEffectManager: SoundEffectManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scoreRepository = ScoreRepository(applicationContext)
        soundEffectManager = SoundEffectManager(applicationContext)
        enableEdgeToEdge()
        setContent {
            NQueensPuzzleTheme {
                NQueensNavHost(
                    viewModel = viewModel,
                    scoreRepository = scoreRepository,
                    soundEffectManager = soundEffectManager,
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundEffectManager.release()
    }
}
