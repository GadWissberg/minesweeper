package com.gadarts.minesweeper.components

import com.badlogic.ashley.core.Entity

class TileComponent : GameComponent {
    var revealed: Boolean = false
    var crate: Entity? = null

    override fun reset() {

    }

}
