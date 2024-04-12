package com.gadarts.minesweeper.components

import com.badlogic.gdx.graphics.g3d.ModelInstance


class ModelInstanceComponent : GameComponent {
    var visible: Boolean = true
    lateinit var modelInstance: ModelInstance
        private set
    var manualRendering = false
        private set

    fun init(modelInstance: ModelInstance, manualRendering: Boolean = false) {
        this.modelInstance = modelInstance
        this.manualRendering = manualRendering
    }

    override fun reset() {

    }
}
