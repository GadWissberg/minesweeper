package com.gadarts.minesweeper.systems.map.react

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.utils.Timer
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.map.MapSystem
import com.gadarts.minesweeper.systems.map.MutableCellPosition
import com.gadarts.minesweeper.systems.player.PlayerUtils
import kotlin.math.max
import kotlin.math.min

class MapSystemOnPowerupActivated(private val mapSystem: MapSystem) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        tiles: Array<Array<Entity?>>
    ) {
        val currentPosition = PlayerUtils.getPlayerCellPosition(playerData, auxCellPosition1)
        for (row in max(currentPosition.row - 1, 0)..min(currentPosition.row + 1, tiles.size - 1)) {
            for (col in max(currentPosition.col - 1, 0)..min(
                currentPosition.col + 1,
                tiles[0].size - 1
            )) {
                if ((row != currentPosition.row || col != currentPosition.col) && !ComponentsMappers.tile.get(
                        tiles[row][col]
                    ).revealed
                ) {
                    Timer.schedule(
                        object : Timer.Task() {
                            override fun run() {
                                if (GameSessionData.testMapValues[row][col] == 0) {
                                    mapSystem.revealTile(row, col)
                                } else if (GameSessionData.testMapValues[row][col] == 1) {
                                    services.dispatcher.dispatchMessage(
                                        SystemEvents.MINE_TRIGGERED.ordinal,
                                        auxCellPosition2.set(row, col)
                                    )
                                }
                            }
                        },
                        ((col - (currentPosition.col - 1) + (row - (currentPosition.row - 1)) * 3).toFloat()) * 0.25F
                    )
                }
            }
        }
    }

    companion object {
        val auxCellPosition1: MutableCellPosition = MutableCellPosition()
        val auxCellPosition2: MutableCellPosition = MutableCellPosition()
    }

}