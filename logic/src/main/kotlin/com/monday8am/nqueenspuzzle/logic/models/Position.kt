package com.monday8am.nqueenspuzzle.logic.models

@JvmInline
value class Position(private val packed: Int) {
    val row: Int get() = packed shr 16
    val col: Int get() = packed and 0xFFFF

    operator fun component1() = row
    operator fun component2() = col

    override fun toString(): String = "Position(row=$row, col=$col)"

    companion object {
        operator fun invoke(row: Int, col: Int) = Position((row shl 16) or (col and 0xFFFF))
    }
}
