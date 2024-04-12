package com.gadarts.minesweeper.systems.data

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable

class GameSessionData : Disposable {

    var mapData: Array<Array<TileData>> = Array(testMapValues.size) { row ->
        Array(testMapValues[0].size) { col ->
            TileData(row, col)
        }
    }
    val physicsData = PhysicsData()
    val playerData = PlayerData()
    lateinit var camera: PerspectiveCamera
    lateinit var stage: Stage
    lateinit var particleSystem: ParticleSystem

    override fun dispose() {
        physicsData.dispose()
        playerData.dispose()
    }

    companion object {
        const val TEMP_GROUND_SIZE = 10
        val testMapValues = arrayOf(
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
