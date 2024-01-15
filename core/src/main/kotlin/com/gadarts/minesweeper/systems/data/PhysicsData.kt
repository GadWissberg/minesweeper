package com.gadarts.minesweeper.systems.data

import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.utils.Disposable
import com.gadarts.minesweeper.systems.CollisionShapesDebugDrawing

class PhysicsData : Disposable {
    var debugDrawingMethod: CollisionShapesDebugDrawing? = null
    lateinit var collisionWorld: btDiscreteDynamicsWorld
    override fun dispose() {
        collisionWorld.dispose()
    }

}
