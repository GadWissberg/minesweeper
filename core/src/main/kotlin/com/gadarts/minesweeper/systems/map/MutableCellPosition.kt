package com.gadarts.minesweeper.systems.map

class MutableCellPosition {
    var col: Int = 0
    var row: Int = 0

    fun set(row: Int, col: Int): MutableCellPosition {
        this.row = row
        this.col = col
        return this
    }

}
