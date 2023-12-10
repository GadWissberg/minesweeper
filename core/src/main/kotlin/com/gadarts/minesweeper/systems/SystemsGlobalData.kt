package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.PerspectiveCamera

class SystemsGlobalData {

    var player: Entity? = null
    lateinit var camera: PerspectiveCamera

    companion object {
        const val TEMP_GROUND_SIZE = 10
        val values = arrayOf(
            arrayOf(0, 0, 0, 0, 2, 0, 0, 0, 0, 0),
            arrayOf(0, 4, 0, 0, 0, 1, 1, 0, 0, 0),
            arrayOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 1, 4, 0, 0, 0),
            arrayOf(0, 0, 0, 1, 1, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 1, 0, 0, 4, 0, 0),
            arrayOf(0, 0, 0, 4, 0, 0, 1, 0, 0, 0),
            arrayOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 3, 0, 0, 0, 0, 0),
        )

    }
}
