package com.gadarts.minesweeper.systems.map

interface MapSystem {
    fun revealTile(currentRow: Int, currentCol: Int)
    fun sumMinesAround(currentRow: Int, currentCol: Int): Int

}
