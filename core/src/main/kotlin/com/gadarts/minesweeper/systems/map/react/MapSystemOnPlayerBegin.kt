package com.gadarts.minesweeper.systems.map.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.data.TileData
import com.gadarts.minesweeper.systems.map.MapSystem
import com.gadarts.minesweeper.systems.map.MutableCellPosition
import com.gadarts.minesweeper.systems.player.PlayerUtils

class MapSystemOnPlayerBegin(private val mapSystem: MapSystem) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        mapData: Array<Array<TileData>>
    ) {
        PlayerUtils.getPlayerCellPosition(playerData, auxCell)
        mapSystem.sumMinesAround(
            auxCell.row,
            auxCell.col
        )
    }

    companion object {
        private val auxCell = MutableCellPosition()
    }
}
