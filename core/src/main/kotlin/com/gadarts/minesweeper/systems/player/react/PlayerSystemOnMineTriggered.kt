package com.gadarts.minesweeper.systems.player.react

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.map.MutableTilePosition
import com.gadarts.minesweeper.systems.player.PlayerUtils

class PlayerSystemOnMineTriggered : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        tiles: Array<Array<Entity?>>
    ) {
        val mineCell = msg.extraInfo as MutableTilePosition
        if (playerData.invulnerableStepsLeft <= 0 && PlayerUtils.getPlayerTilePosition(
                playerData,
                auxCellPosition
            ).equalsToCell(mineCell)
        ) {
            playerData.reset()
            services.dispatcher.dispatchMessage(SystemEvents.PLAYER_BLOWN.ordinal)
        }
    }

    companion object {
        val auxCellPosition = MutableTilePosition()
    }
}
