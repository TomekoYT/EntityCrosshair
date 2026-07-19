package tomeko.entitycrosshair.utils

data class Position(val x: Int, val y: Int)

fun positionToIndex(x: Int, y: Int): Int = x + y * 32

fun indexToPosition(index: Int): Position = Position(index % 32, index / 32)