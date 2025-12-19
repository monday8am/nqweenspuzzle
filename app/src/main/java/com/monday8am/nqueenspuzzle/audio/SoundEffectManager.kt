package com.monday8am.nqueenspuzzle.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

enum class SoundEffect {
    QUEEN_PLACED, // Positive feedback sound
    QUEEN_CONFLICT, // Warning/alarm sound
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

    private val sounds: Map<SoundEffect, Int> = emptyMap()
    // TODO: Load actual sound files when they're added to res/raw/
    // Example:
    // mapOf(
    //     SoundEffect.QUEEN_PLACED to soundPool.load(context, R.raw.queen_placed, 1),
    //     SoundEffect.QUEEN_CONFLICT to soundPool.load(context, R.raw.queen_conflict, 1),
    //     SoundEffect.GAME_WON to soundPool.load(context, R.raw.game_won, 1)
    // )

    fun play(effect: SoundEffect) {
        sounds[effect]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
