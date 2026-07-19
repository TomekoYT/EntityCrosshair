package tomeko.entitycrosshair.utils

private const val max = 32

data class Position(val x: Int, val y: Int)

fun positionToIndex(x: Int, y: Int): Int = x + y * max

fun indexToPosition(index: Int): Position = Position(index % max, index / max)