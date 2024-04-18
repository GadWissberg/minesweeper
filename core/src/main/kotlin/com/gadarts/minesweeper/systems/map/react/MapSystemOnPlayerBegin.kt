package com.gadarts.minesweeper.systems.map.react

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.map.MapSystem
import com.gadarts.minesweeper.systems.map.MutableCellPosition
import com.gadarts.minesweeper.systems.player.PlayerUtils

class MapSystemOnPlayerBegin(private val mapSystem: MapSystem) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        tiles: Array<Array<Entity?>>
    ) {
        PlayerUtils.getPlayerCellPosition(playerData, auxCell)
        mapSystem.sumMinesAround(
            auxCell.row,
            auxCell.col
        )
        tiles.forEach {
            it.forEach { tile ->
                if (tile != null) {
                    ComponentsMappers.tile.get(tile).revealed = false
                }
            }
        }
    }

    companion object {
        private val auxCell = MutableCellPosition()
    }
}
