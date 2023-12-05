package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.PerspectiveCamera

class SystemsGlobalData {

    lateinit var player: Entity
    lateinit var camera: PerspectiveCamera

    companion object {
        const val TEMP_GROUND_SIZE = 10

    }
}
