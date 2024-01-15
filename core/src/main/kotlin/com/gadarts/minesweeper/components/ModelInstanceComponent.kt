package com.gadarts.minesweeper.components

import com.badlogic.gdx.graphics.g3d.ModelInstance


class ModelInstanceComponent : GameComponent {
    var visible: Boolean = true
    lateinit var modelInstance: ModelInstance
        private set

    fun init(modelInstance: ModelInstance) {
        this.modelInstance = modelInstance
    }

    override fun reset() {

    }
}
