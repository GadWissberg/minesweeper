package com.gadarts.minesweeper.systems.map

class TileCalculatedResult {
    var col: Int = 0
    var row: Int = 0
    var sum: Int = 0

    fun set(destRow: Int, destCol: Int, sum: Int): TileCalculatedResult {
        this.row = destRow
        this.col = destCol
        this.sum = sum
        return this
    }

}
