package com.gadarts.minesweeper.systems.map.react

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.utils.Timer
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.map.MapSystem
import com.gadarts.minesweeper.systems.map.MutableTilePosition
import com.gadarts.minesweeper.systems.player.PlayerUtils
import kotlin.math.max
import kotlin.math.min

class MapSystemOnPowerupActivated(private val mapSystem: MapSystem) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        managers: Managers
    ) {
        val currentPosition =
            PlayerUtils.getPlayerTilePosition(gameSessionData.playerData, auxCellPosition1)
        val sound = managers.assetsManager.getAssetByDefinition(
            SoundsDefinitions.TILE_REVEALED
        )
        for (row in max(currentPosition.row - 1, 0)..min(
            currentPosition.row + 1,
            gameSessionData.tiles.size - 1
        )) {
            for (col in max(currentPosition.col - 1, 0)..min(
                currentPosition.col + 1,
                gameSessionData.tiles[0].size - 1
            )) {
                if ((row != currentPosition.row || col != currentPosition.col) && !ComponentsMappers.tile.get(
                        gameSessionData.tiles[row][col]
                    ).revealed
                ) {
                    Timer.schedule(
                        object : Timer.Task() {
                            override fun run() {
                                if (gameSessionData.testMapValues[row][col] == 0) {
                                    mapSystem.revealTile(row, col)
                                    managers.soundPlayer.playSound(sound)
                                } else if (gameSessionData.testMapValues[row][col] == 1) {
                                    mapSystem.triggerMine(auxCellPosition2.set(row, col))
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
        val auxCellPosition1: MutableTilePosition = MutableTilePosition()
        val auxCellPosition2: MutableTilePosition = MutableTilePosition()
    }

}
