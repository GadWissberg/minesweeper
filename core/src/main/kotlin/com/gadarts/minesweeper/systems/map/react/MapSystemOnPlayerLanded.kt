package com.gadarts.minesweeper.systems.map.react

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.map.MapSystem
import com.gadarts.minesweeper.systems.map.MutableTilePosition
import kotlin.math.max
import kotlin.math.min

class MapSystemOnPlayerLanded(private val mapSystem: MapSystem) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        managers: Managers
    ) {
        val position =
            ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.transform.getTranslation(
                auxVector
            )
        val currentRow = position.z.toInt()
        val currentCol = position.x.toInt()
        val currentValue = gameSessionData.testMapValues[currentRow][currentCol]
        if (currentValue == 1 || currentValue == 3) {
            if (currentValue == 3) {
                managers.soundPlayer.playSoundByDefinition(SoundsDefinitions.WIN)
            }
            mapSystem.triggerMine(auxCellPosition.set(currentRow, currentCol))
        } else {
            mapSystem.revealTile(currentRow, currentCol)
        }
        playCowSound(currentRow, currentCol, gameSessionData, managers)
    }

    private fun playCowSound(
        currentRow: Int,
        currentCol: Int,
        gameSessionData: GameSessionData,
        managers: Managers
    ) {
        if (MathUtils.random() < 0.5F) {
            for (row in currentRow - 1..currentRow + 1) {
                for (col in currentCol - 1..currentCol + 1) {
                    if (gameSessionData.testMapValues[min(
                            max(row, 0),
                            gameSessionData.testMapValues.size - 1
                        )][min(
                            max(col, 0),
                            gameSessionData.testMapValues[0].size - 1
                        )] == 6
                    ) {
                        managers.soundPlayer.playSoundByDefinition(SoundsDefinitions.COW)
                        break
                    }
                }
            }
        }
    }

    companion object {
        private val auxVector = Vector3()
        private val auxCellPosition = MutableTilePosition()
    }
}
