package com.gadarts.minesweeper.systems.data

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable

class GameSessionData : Disposable {

    lateinit var testMapValues: Array<Array<Int>>
    lateinit var tiles: Array<Array<Entity?>>
    val physicsData = PhysicsData()
    val playerData = PlayerData()
    lateinit var camera: PerspectiveCamera
    lateinit var stage: Stage
    lateinit var particleSystem: ParticleSystem

    init {
        createTempArray()
    }

    private fun createTempArray() {
        testMapValues = arrayOf(
            arrayOf(1, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0),
            arrayOf(0, 4, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0),
            arrayOf(0, 0, 5, 1, 0, 1, 0, 0, 0, 5, 1, 0),
            arrayOf(0, 0, 0, 1, 0, 0, 4, 0, 0, 0, 0, 1),
            arrayOf(0, 0, 1, 1, 1, 0, 0, 0, 4, 1, 4, 0),
            arrayOf(0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0),
            arrayOf(1, 0, 0, 0, 1, 0, 0, 4, 0, 0, 0, 1),
            arrayOf(0, 1, 0, 4, 0, 0, 1, 0, 0, 0, 1, 0),
            arrayOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 4, 5, 1, 0, 4, 0, 1, 4, 0, 1, 0),
            arrayOf(0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0),
        )
    }

    override fun dispose() {
        physicsData.dispose()
        playerData.dispose()
    }

}
