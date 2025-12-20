package com.monday8am.nqueenspuzzle.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.monday8am.nqueenspuzzle.R

enum class SoundEffect {
    QUEEN_PLACED, // Positive feedback sound
    QUEEN_CONFLICT, // Warning/alarm sound
    RESET_GAME, // Resetting board sound
    GAME_WON, // Celebration sound
}

class SoundEffectManager(
    context: Context,
) {
    private val soundPool: SoundPool =
        SoundPool
            .Builder()
            .setMaxStreams(3)
            .setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            ).build()

    private val sounds: Map<SoundEffect, Int> =
        mapOf(
            SoundEffect.QUEEN_PLACED to soundPool.load(context, R.raw.positive_action, 1),
            SoundEffect.QUEEN_CONFLICT to soundPool.load(context, R.raw.negative_action, 1),
            SoundEffect.RESET_GAME to soundPool.load(context, R.raw.reset_game, 1),
            SoundEffect.GAME_WON to soundPool.load(context, R.raw.game_won, 1),
        )

    fun play(effect: SoundEffect) {
        sounds[effect]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
