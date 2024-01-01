package com.gadarts.minesweeper.systems.data

import com.badlogic.ashley.core.Entity

class TileData(val row: Int, val col: Int) {

    var crate: Entity? = null
}
