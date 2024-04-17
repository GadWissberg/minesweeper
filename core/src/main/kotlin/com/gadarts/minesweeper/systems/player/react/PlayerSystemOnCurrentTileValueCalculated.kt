package com.gadarts.minesweeper.systems.player.react

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.data.TileData

class PlayerSystemOnCurrentTileValueCalculated : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        mapData: Array<Array<TileData>>
    ) {
        val definition = sumToTextureDefinition[msg.extraInfo as Int]
        if (definition != null) {
            ComponentsMappers.modelInstance.get(playerData.digit).visible = true
            (ComponentsMappers.modelInstance.get(playerData.digit).modelInstance.materials.get(
                0
            ).get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
                services.assetsManager.getAssetByDefinition(definition)
        } else {
            ComponentsMappers.modelInstance.get(playerData.digit).visible = false
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
    }
}
