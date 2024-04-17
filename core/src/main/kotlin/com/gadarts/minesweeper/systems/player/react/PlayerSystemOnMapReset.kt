package com.gadarts.minesweeper.systems.player.react

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.data.TileData
import com.gadarts.minesweeper.systems.player.PlayerSystem

class PlayerSystemOnMapReset(private val playerSystem: PlayerSystem) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        mapData: Array<Array<TileData>>
    ) {
        ComponentsMappers.modelInstance.get(playerData.digit).visible = false
        services.engine.removeEntity(playerData.player)
        playerData.player = null
        Gdx.app.postRunnable {
            playerSystem.addPlayer(true)
            services.dispatcher.dispatchMessage(SystemEvents.PLAYER_BEGIN.ordinal)
        }
    }
}
