package com.gadarts.minesweeper.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Screen
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.systems.Systems
import com.gadarts.minesweeper.systems.SystemsGlobalData


class GamePlayScreen(private val assetsManager: GameAssetManager) : Screen {
    private lateinit var engine: PooledEngine

    override fun show() {
        engine = PooledEngine()
        val systemsGlobalData = SystemsGlobalData()
        val systems = Systems.entries.toTypedArray()
        val soundPlayer = SoundPlayer(assetsManager)
        systems.forEach { system ->
            engine.addSystem(system.systemInstance)
            systems.forEach { listener ->
                system.systemInstance.addListener(
                    listener.systemInstance
                )
            }
            system.systemInstance.createGlobalData(systemsGlobalData, assetsManager, soundPlayer)
        }
        systems.forEach { it.systemInstance.onGlobalDataReady() }
    }

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
    }

}
