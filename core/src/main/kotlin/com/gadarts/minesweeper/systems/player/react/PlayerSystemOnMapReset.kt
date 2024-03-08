package com.gadarts.minesweeper.systems.player.react

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.player.PlayerSystem

class PlayerSystemOnMapReset(private val playerSystem: PlayerSystem) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        assetsManger: GameAssetManager,
        dispatcher: MessageDispatcher,
        engine: Engine
    ) {
        ComponentsMappers.modelInstance.get(playerData.digit).visible = false
        engine.removeEntity(playerData.player)
        playerData.player = null
        Gdx.app.postRunnable {
            playerSystem.addPlayer(true)
            dispatcher.dispatchMessage(SystemEvents.PLAYER_BEGIN.ordinal)
        }
    }

}
