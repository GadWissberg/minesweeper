package com.gadarts.minesweeper.systems.map.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.map.MapSystem
import com.gadarts.minesweeper.systems.map.MutableTilePosition
import com.gadarts.minesweeper.systems.player.PlayerUtils

class MapSystemOnPlayerBegin(private val mapSystem: MapSystem) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        services: Services
    ) {
        PlayerUtils.getPlayerTilePosition(gameSessionData.playerData, auxCell)
        mapSystem.sumMinesAround(
            auxCell.row,
            auxCell.col
        )
        gameSessionData.tiles.forEach {
            it.forEach { tile ->
                if (tile != null) {
                    ComponentsMappers.tile.get(tile).revealed = false
                }
            }
        }
    }

    companion object {
        private val auxCell = MutableTilePosition()
    }
}
