package com.gadarts.minesweeper.systems.player

import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.map.MutableTilePosition

object PlayerUtils {
    private val auxVector = Vector3()

    fun getPlayerTilePosition(
        playerData: PlayerData,
        output: MutableTilePosition
    ): MutableTilePosition {
        val position =
            ComponentsMappers.modelInstance.get(playerData.player).modelInstance.transform.getTranslation(
                auxVector
            )
        val currentRow = position.z.toInt()
        val currentCol = position.x.toInt()
        return output.set(currentRow, currentCol)
    }

}
