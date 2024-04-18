package com.gadarts.minesweeper.systems.map.react

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.utils.Timer
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.TileComponent
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData

class MapSystemOnPlayerBlown :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        tiles: Array<Array<Entity?>>
    ) {
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                val tilesEntities =
                    services.engine.getEntitiesFor(Family.all(TileComponent::class.java).get())
                val unrevealedTexture =
                    services.assetsManager.getAssetByDefinition(TexturesDefinitions.TILE_UNREVEALED)
                val destinationTexture =
                    services.assetsManager.getAssetByDefinition(TexturesDefinitions.TILE_DESTINATION)
                for (tile in tilesEntities) {
                    val textureDescription =
                        (ComponentsMappers.modelInstance.get(tile).modelInstance.materials.get(0)
                            .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription
                    textureDescription.texture =
                        if (textureDescription.texture != destinationTexture)
                            unrevealedTexture else destinationTexture
                }
                services.dispatcher.dispatchMessage(SystemEvents.MAP_RESET.ordinal)
            }
        }, 5F)
    }

}
