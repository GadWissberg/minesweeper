package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable

class SystemsGlobalData : Disposable {

    lateinit var stage: Stage
    var debugDrawingMethod: CollisionShapesDebugDrawing? = null
    lateinit var collisionWorld: btDiscreteDynamicsWorld
    var player: Entity? = null
    lateinit var camera: PerspectiveCamera
    lateinit var particleSystem: ParticleSystem
    override fun dispose() {
        collisionWorld.dispose()
    }

    companion object {
        const val TEMP_GROUND_SIZE = 10
        val values = arrayOf(
            arrayOf(0, 0, 0, 0, 2, 0, 0, 0, 0, 0),
            arrayOf(0, 4, 0, 0, 0, 1, 1, 0, 0, 0),
            arrayOf(0, 0, 5, 1, 0, 0, 0, 0, 0, 0),
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
