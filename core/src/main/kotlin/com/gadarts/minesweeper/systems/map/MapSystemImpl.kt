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
import com.gadarts.minesweeper.systems.data.GameSessionData.Companion.TEMP_GROUND_SIZE
import com.gadarts.minesweeper.systems.map.react.MapSystemOnPlayerBegin
import com.gadarts.minesweeper.systems.map.react.MapSystemOnPlayerBlown
import com.gadarts.minesweeper.systems.map.react.MapSystemOnPlayerLanded
import com.gadarts.minesweeper.systems.map.react.MapSystemOnPowerupActivated
import kotlin.math.max
import kotlin.math.min


class MapSystemImpl : GameEntitySystem(), MapSystem {

    private lateinit var tileModel: Model
    private var tiles: Array<Array<Entity?>> = Array(TEMP_GROUND_SIZE) {
        arrayOfNulls<Entity?>(TEMP_GROUND_SIZE)
    }
    private lateinit var lineGrid: Model

    override fun initialize(gameSessionData: GameSessionData, services: Services) {
        super.initialize(gameSessionData, services)
        tileModel = GameUtils.createTileModel(ModelBuilder(), services.assetsManager)
        for (row in GameSessionData.testMapValues.indices) {
            for (col in GameSessionData.testMapValues[row].indices) {
                val tileModelInstance = ModelInstance(tileModel)
                val tileEntity = EntityBuilder.beginBuildingEntity(engine)
                    .addModelInstanceComponent(
                        tileModelInstance,
                        auxVector.set(col.toFloat(), 0F, row.toFloat())
                    )
                    .addTileComponent()
                    .finishAndAddToEngine()
                tiles[row][col] = tileEntity
                if (GameSessionData.testMapValues[row][col] == 3) {
                    (tileModelInstance.materials.get(0)
                        .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
                        services.assetsManager.getAssetByDefinition(TexturesDefinitions.TILE_DESTINATION)
                } else if (GameSessionData.testMapValues[row][col] == 4) {
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
    }

    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(
            SystemEvents.PLAYER_LANDED,
            SystemEvents.PLAYER_BEGIN,
            SystemEvents.PLAYER_BLOWN
        )
    }

    override fun revealTile(currentRow: Int, currentCol: Int) {
        (ComponentsMappers.modelInstance.get(tiles[currentRow][currentCol]).modelInstance.materials[0].get(
            TextureAttribute.Diffuse
        ) as TextureAttribute).textureDescription.texture =
            services.assetsManager.getAssetByDefinition(
                sumToTextureDefinition[sumMinesAround(currentRow, currentCol)]
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
            SystemEvents.POWERUP_ACTIVATED to MapSystemOnPowerupActivated(),
        )
    }

    override fun sumMinesAround(currentRow: Int, currentCol: Int): Int {
        var sum = 0
        for (row in max(currentRow - 1, 0)..min(currentRow + 1, tiles.size - 1)) {
            for (col in max(currentCol - 1, 0)..min(currentCol + 1, tiles[0].size - 1)) {
                if (GameSessionData.testMapValues[row][col] == 1 && (row != currentRow || col != currentCol)) {
                    sum += 1
                }
            }
        }
        services.dispatcher.dispatchMessage(SystemEvents.CURRENT_TILE_VALUE_CALCULATED.ordinal, sum)
        return sum
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
    }

}
