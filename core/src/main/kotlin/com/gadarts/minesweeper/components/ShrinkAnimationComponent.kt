package com.gadarts.minesweeper.components

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3

class ShrinkAnimationComponent : GameComponent {
    var stepSize: Float = 0.0f
        private set
    lateinit var interpolation: Interpolation
        private set
    val scaleTarget = Vector3()
    var shrink: Boolean = false
    var animationProgress: Float = 0F
    private val initialTransform = Matrix4()

    override fun reset() {

    }

    fun init(
        scaleTargetX: Float,
        scaleTargetY: Float,
        scaleTargetZ: Float,
        initialTransform: Matrix4,
        interpolation: Interpolation,
        stepSize: Float
    ) {
        scaleTarget.set(scaleTargetX, scaleTargetY, scaleTargetZ)
        this.initialTransform.set(initialTransform)
        this.interpolation = interpolation
        this.stepSize = stepSize
    }

    fun getInitialTransform(output: Matrix4): Matrix4 {
        return output.set(initialTransform)
    }

}
