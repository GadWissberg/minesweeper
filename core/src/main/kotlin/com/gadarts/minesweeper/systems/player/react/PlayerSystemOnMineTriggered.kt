package com.gadarts.minesweeper.systems.player.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.map.MutableTilePosition
import com.gadarts.minesweeper.systems.player.PlayerUtils

class PlayerSystemOnMineTriggered : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        services: Services
    ) {
        val mineCell = msg.extraInfo as MutableTilePosition
        if (gameSessionData.playerData.invulnerableStepsLeft <= 0 && PlayerUtils.getPlayerTilePosition(
                gameSessionData.playerData,
                auxCellPosition
            ).equalsToCell(mineCell)
        ) {
            gameSessionData.playerData.reset()
            ComponentsMappers.modelInstance.get(gameSessionData.playerData.digit).visible = false
            services.dispatcher.dispatchMessage(SystemEvents.PLAYER_BLOWN.ordinal)
        }
    }

    companion object {
        val auxCellPosition = MutableTilePosition()
    }
}
