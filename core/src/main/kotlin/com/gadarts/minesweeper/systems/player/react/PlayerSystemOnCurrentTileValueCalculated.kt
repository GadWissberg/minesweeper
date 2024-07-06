package com.gadarts.minesweeper.systems.player.react

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.map.MutableTilePosition
import com.gadarts.minesweeper.systems.map.TileCalculatedResult
import com.gadarts.minesweeper.systems.player.PlayerUtils

class PlayerSystemOnCurrentTileValueCalculated : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        managers: Managers
    ) {
        val tileCalculatedResult = msg.extraInfo as TileCalculatedResult
        if (!PlayerUtils.getPlayerTilePosition(gameSessionData.playerData, auxCell)
                .equalsToCell(tileCalculatedResult.row, tileCalculatedResult.col)
        ) return

        val definition = sumToTextureDefinition[(msg.extraInfo as TileCalculatedResult).sum]
        if (definition != null) {
            ComponentsMappers.modelInstance.get(gameSessionData.playerData.digit).visible = true
            (ComponentsMappers.modelInstance.get(gameSessionData.playerData.digit).modelInstance.materials.get(
                0
            ).get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
                managers.assetsManager.getAssetByDefinition(definition)
        } else {
            ComponentsMappers.modelInstance.get(gameSessionData.playerData.digit).visible = false
        }
    }

    companion object {
        private val sumToTextureDefinition = listOf(
            null,
            TexturesDefinitions.DIGIT_1,
            TexturesDefinitions.DIGIT_2,
            TexturesDefinitions.DIGIT_3,
            TexturesDefinitions.DIGIT_4,
            TexturesDefinitions.DIGIT_5,
            TexturesDefinitions.DIGIT_6,
            TexturesDefinitions.DIGIT_7,
        )
        private val auxCell = MutableTilePosition()
    }
}
