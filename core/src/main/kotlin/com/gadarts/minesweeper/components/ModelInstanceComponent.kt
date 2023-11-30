package com.gadarts.minesweeper.components

import com.badlogic.gdx.graphics.g3d.ModelInstance


class ModelInstanceComponent : GameComponent {
    lateinit var modelInstance: ModelInstance
        private set

    fun init(modelInstance: ModelInstance) {
        this.modelInstance = modelInstance
    }

    override fun reset() {

    }
}
