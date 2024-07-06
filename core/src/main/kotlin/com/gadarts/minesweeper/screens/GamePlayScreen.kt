package com.gadarts.minesweeper.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.systems.Systems
import com.gadarts.minesweeper.systems.data.GameSessionData


class GamePlayScreen(private val assetsManager: GameAssetManager) : Screen {
    private lateinit var gameSessionData: GameSessionData
    private lateinit var engine: PooledEngine

    override fun show() {
        engine = PooledEngine()
        gameSessionData = GameSessionData()
        val systems = Systems.entries.toTypedArray()
        val soundPlayer = SoundPlayer(assetsManager)
        val dispatcher = MessageDispatcher()
        val managers = Managers(engine, soundPlayer, assetsManager, dispatcher)
        systems.forEach { system ->
            engine.addSystem(system.systemInstance)
            system.systemInstance.initialize(
                gameSessionData,
                managers
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
        gameSessionData.physicsData.collisionWorld.dispose()
    }

}
