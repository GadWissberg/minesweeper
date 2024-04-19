package com.gadarts.minesweeper.systems.map

class MutableTilePosition {
    var col: Int = 0
    var row: Int = 0

    fun set(row: Int, col: Int): MutableTilePosition {
        this.row = row
        this.col = col
        return this
    }

    fun equalsToCell(cell: MutableTilePosition): Boolean {
        return equalsToCell(cell.row, cell.col)
    }

    fun equalsToCell(row: Int, col: Int): Boolean {
        return this.col == col && this.row == row
    }
}
