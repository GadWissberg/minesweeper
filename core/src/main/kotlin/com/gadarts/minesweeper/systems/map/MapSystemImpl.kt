package com.gadarts.minesweeper.systems.map

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.assets.ModelsDefinitions
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.GameUtils
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.map.react.MapSystemOnPlayerBegin
import com.gadarts.minesweeper.systems.map.react.MapSystemOnPlayerBlown
import com.gadarts.minesweeper.systems.map.react.MapSystemOnPlayerLanded
import com.gadarts.minesweeper.systems.map.react.MapSystemOnPowerupActivated
import kotlin.math.max
import kotlin.math.min


class MapSystemImpl : GameEntitySystem(), MapSystem {

    private lateinit var tileModel: Model
    private lateinit var lineGrid: Model

    override fun initialize(gameSessionData: GameSessionData, services: Services) {
        super.initialize(gameSessionData, services)
        tileModel = GameUtils.createTileModel(ModelBuilder(), services.assetsManager)
        val tiles = Array(gameSessionData.testMapValues.size) {
            arrayOfNulls<Entity?>(gameSessionData.testMapValues.size)
        }
        for (row in gameSessionData.testMapValues.indices) {
            for (col in gameSessionData.testMapValues[row].indices) {
                val tileModelInstance = ModelInstance(tileModel)
                val tileEntity = EntityBuilder.beginBuildingEntity(engine)
                    .addModelInstanceComponent(
                        tileModelInstance,
                        auxVector.set(col.toFloat(), 0F, row.toFloat())
                    )
                    .addTileComponent()
                    .finishAndAddToEngine()
                tiles[row][col] = tileEntity
                if (gameSessionData.testMapValues[row][col] == 3) {
                    (tileModelInstance.materials.get(0)
                        .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
                        services.assetsManager.getAssetByDefinition(TexturesDefinitions.TILE_DESTINATION)
                } else if (gameSessionData.testMapValues[row][col] == 4) {
                    val modelInstance = ModelInstance(
                        services.assetsManager.getAssetByDefinition(ModelsDefinitions.ROCK)
                    )
                    EntityBuilder.beginBuildingEntity(engine)
                        .addModelInstanceComponent(
                            modelInstance, Vector3(col + 0.5F, 0F, row + 0.5F)
                        ).finishAndAddToEngine()
                }
            }
        }
        gameSessionData.tiles = tiles
    }

    override fun revealTile(destRow: Int, destCol: Int) {
        if (destRow < 0 || destRow >= gameSessionData.tiles.size || destCol < 0 || destCol >= gameSessionData.tiles[0].size) return

        val sum = sumMinesAround(destRow, destCol)
        (ComponentsMappers.modelInstance.get(gameSessionData.tiles[destRow][destCol]).modelInstance.materials[0].get(
            TextureAttribute.Diffuse
        ) as TextureAttribute).textureDescription.texture =
            services.assetsManager.getAssetByDefinition(sumToTextureDefinition[sum])
        ComponentsMappers.tile.get(gameSessionData.tiles[destRow][destCol]).revealed = true
        services.dispatcher.dispatchMessage(
            SystemEvents.TILE_REVEALED.ordinal,
            tileCalculatedResult.set(destRow, destCol, sum)
        )
    }


    override fun onSystemReady() {
    }

    override fun dispose() {
        lineGrid.dispose()
        tileModel.dispose()
    }

    override fun getSubscribedEvents(): Map<SystemEvents, HandlerOnEvent> {
        return mapOf(
            SystemEvents.PLAYER_LANDED to MapSystemOnPlayerLanded(this),
            SystemEvents.PLAYER_BEGIN to MapSystemOnPlayerBegin(this),
            SystemEvents.PLAYER_BLOWN to MapSystemOnPlayerBlown(),
            SystemEvents.POWERUP_ACTIVATED to MapSystemOnPowerupActivated(this),
        )
    }

    override fun sumMinesAround(destRow: Int, destCol: Int): Int {
        if (destRow < 0 || destRow >= gameSessionData.tiles.size || destCol < 0 || destCol >= gameSessionData.tiles[0].size) return 0

        var sum = 0
        for (row in max(destRow - 1, 0)..min(destRow + 1, gameSessionData.tiles.size - 1)) {
            for (col in max(destCol - 1, 0)..min(destCol + 1, gameSessionData.tiles[0].size - 1)) {
                if (gameSessionData.testMapValues[row][col] == 1 && (row != destRow || col != destCol)) {
                    sum += 1
                }
            }
        }
        return sum
    }

    override fun triggerMine(position: MutableTilePosition) {
        services.dispatcher.dispatchMessage(SystemEvents.MINE_TRIGGERED.ordinal, position)
        gameSessionData.testMapValues[position.row][position.col] = 0
        (ComponentsMappers.modelInstance.get(gameSessionData.tiles[position.row][position.col]).modelInstance.materials[0].get(
            TextureAttribute.Diffuse
        ) as TextureAttribute).textureDescription.texture =
            services.assetsManager.getAssetByDefinition(TexturesDefinitions.TILE_BOMBED)
    }

    companion object {
        private val auxVector = Vector3()
        private val sumToTextureDefinition = listOf(
            TexturesDefinitions.TILE_0,
            TexturesDefinitions.TILE_1,
            TexturesDefinitions.TILE_2,
            TexturesDefinitions.TILE_3,
            TexturesDefinitions.TILE_4,
            TexturesDefinitions.TILE_5,
            TexturesDefinitions.TILE_6,
            TexturesDefinitions.TILE_7,
            TexturesDefinitions.TILE_8,
        )
        private val tileCalculatedResult = TileCalculatedResult()
    }

}
