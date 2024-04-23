package com.gadarts.minesweeper.systems.map

interface MapSystem {
    fun revealTile(destRow: Int, destCol: Int)
    fun sumMinesAround(destRow: Int, destCol: Int): Int
    fun triggerMine(position: MutableTilePosition)

}
