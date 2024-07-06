package com.gadarts.minesweeper.systems.player.react

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.player.PlayerSystem

class PlayerSystemOnMapReset(private val playerSystem: PlayerSystem) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        managers: Managers
    ) {
        ComponentsMappers.modelInstance.get(gameSessionData.playerData.digit).visible = false
        managers.dispatcher.dispatchMessage(SystemEvents.PLAYER_IS_ABOUT_TO_BE_REMOVED.ordinal)
        managers.engine.removeEntity(gameSessionData.playerData.player)
        gameSessionData.playerData.player = null
        Gdx.app.postRunnable {
            playerSystem.addPlayer(true)
            playerSystem.playerBegin()
        }
    }
}
