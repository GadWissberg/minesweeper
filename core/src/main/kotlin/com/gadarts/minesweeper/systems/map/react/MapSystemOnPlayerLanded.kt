package com.gadarts.minesweeper.systems.map.react

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.map.MapSystem
import com.gadarts.minesweeper.systems.map.MutableTilePosition

class MapSystemOnPlayerLanded(private val mapSystem: MapSystem) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        tiles: Array<Array<Entity?>>
    ) {
        val position =
            ComponentsMappers.modelInstance.get(playerData.player).modelInstance.transform.getTranslation(
                auxVector
            )
        val currentRow = position.z.toInt()
        val currentCol = position.x.toInt()
        val currentValue = GameSessionData.testMapValues[currentRow][currentCol]
        if (currentValue == 1 || currentValue == 3) {
            if (currentValue == 3) {
                services.soundPlayer.playSoundByDefinition(SoundsDefinitions.WIN)
            }
            services.dispatcher.dispatchMessage(
                SystemEvents.MINE_TRIGGERED.ordinal,
                auxCellPosition.set(currentRow, currentCol)
            )
        } else {
            mapSystem.revealTile(currentRow, currentCol)
        }
    }

    companion object {
        private val auxVector = Vector3()
        private val auxCellPosition = MutableTilePosition()
    }
}
