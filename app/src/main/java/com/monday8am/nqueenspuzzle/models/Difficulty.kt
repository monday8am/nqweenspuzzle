package com.monday8am.nqueenspuzzle.models

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
    ;

    fun getDisplayName(): String =
        when (this) {
            EASY -> "Easy"
            MEDIUM -> "Medium"
            HARD -> "Hard"
        }
}
