package com.gadarts.minesweeper.components

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.collision.BoundingBox


class ModelInstanceComponent : GameComponent {
    var visible: Boolean = true
    private val boundingBox = BoundingBox()
    lateinit var modelInstance: ModelInstance
        private set
    var manualRendering = false
        private set

    fun init(
        modelInstance: ModelInstance,
        boundingBox: BoundingBox,
        manualRendering: Boolean = false
    ) {
        this.modelInstance = modelInstance
        this.manualRendering = manualRendering
        this.boundingBox.set(boundingBox)
    }

    fun getBoundingBox(output: BoundingBox): BoundingBox {
        return output.set(boundingBox)
    }
    override fun reset() {

    }
}
