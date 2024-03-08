package com.gadarts.minesweeper.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.systems.Systems
import com.gadarts.minesweeper.systems.data.SystemsGlobalData


class GamePlayScreen(private val assetsManager: GameAssetManager) : Screen {
    private lateinit var systemsGlobalData: SystemsGlobalData
    private lateinit var engine: PooledEngine

    override fun show() {
        engine = PooledEngine()
        systemsGlobalData = SystemsGlobalData()
        val systems = Systems.entries.toTypedArray()
        val soundPlayer = SoundPlayer(assetsManager)
        val dispatcher = MessageDispatcher()
        systems.forEach { system ->
            engine.addSystem(system.systemInstance)
            system.systemInstance.initialize(
                systemsGlobalData,
                assetsManager,
                soundPlayer,
                dispatcher
            )

        }
        systems.forEach { listener ->
            listener.systemInstance.addListener(
                listener.systemInstance
            )
        }
        systems.forEach { it.systemInstance.onSystemReady() }
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
        systemsGlobalData.physicsData.collisionWorld.dispose()
    }

}
