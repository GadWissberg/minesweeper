package com.gadarts.minesweeper.systems.map

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
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

    private lateinit var backgroundGroundModel: Model
    private lateinit var tileModel: Model
    private lateinit var lineGrid: Model

    override fun initialize(gameSessionData: GameSessionData, services: Services) {
        super.initialize(gameSessionData, services)
        tileModel = GameUtils.createTileModel(ModelBuilder(), services.assetsManager)
        backgroundGroundModel = GameUtils.createTileModel(
            ModelBuilder(),
            services.assetsManager,
            offset = 0.5F,
            size = BACKGROUND_GROUND_SIZE,
            addTextureAttribute = false
        )
        backgroundGroundModel.materials.get(0)
            .set(ColorAttribute.createDiffuse(Color.valueOf("#0e8e3d")))
        val tiles = Array(gameSessionData.testMapValues.size) {
            arrayOfNulls<Entity?>(gameSessionData.testMapValues.size)
        }
        tileModel.calculateBoundingBox(auxBoundingBox)
        for (row in gameSessionData.testMapValues.indices) {
            for (col in gameSessionData.testMapValues[row].indices) {
                val tileModelInstance = ModelInstance(tileModel)
                val tileEntity = EntityBuilder.beginBuildingEntity(engine)
                    .addModelInstanceComponent(
                        tileModelInstance,
                        auxVector.set(col.toFloat(), 0F, row.toFloat()),
                        auxBoundingBox
                    )
                    .addTileComponent()
                    .finishAndAddToEngine()
                tiles[row][col] = tileEntity
                if (gameSessionData.testMapValues[row][col] == 3) {
                    (tileModelInstance.materials.get(0)
                        .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
                        services.assetsManager.getAssetByDefinition(TexturesDefinitions.TILE_DESTINATION)
                } else if (gameSessionData.testMapValues[row][col] == 4) {
                    (tileModelInstance.materials.get(0)
                        .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
                        services.assetsManager.getAssetByDefinition(TexturesDefinitions.TILE_UNOCCUPIED)
                    val modelInstance = ModelInstance(
                        services.assetsManager.getAssetByDefinition(ModelsDefinitions.ROCK)
                    )
                    EntityBuilder.beginBuildingEntity(engine)
                        .addModelInstanceComponent(
                            modelInstance, Vector3(col + 0.5F, 0F, row + 0.5F),
                            services.assetsManager.getCachedBoundingBox(ModelsDefinitions.ROCK)
                        ).finishAndAddToEngine()
                }
            }
        }
        gameSessionData.tiles = tiles
        addLineOfHorizontalFlowers(gameSessionData, services, -1F)
        addLineOfHorizontalFlowers(gameSessionData, services, gameSessionData.tiles.size + 1F)
        addLineOfVerticalFlowers(gameSessionData, services, -1F)
        addLineOfVerticalFlowers(gameSessionData, services, gameSessionData.tiles.size + 1F)
        createHorizontalLineOfTrees(-TREES_OFFSET)
        createHorizontalLineOfTrees(-TREES_OFFSET * 2)
        createHorizontalLineOfTrees(
            gameSessionData.tiles.size + TREES_OFFSET
        )
        createHorizontalLineOfTrees(
            gameSessionData.tiles.size + TREES_OFFSET * 2
        )
        createVerticalLineOfTrees(-TREES_OFFSET)
        createVerticalLineOfTrees(-TREES_OFFSET * 2F)
        createVerticalLineOfTrees(gameSessionData.tiles.size + TREES_OFFSET)
        createVerticalLineOfTrees(gameSessionData.tiles.size + TREES_OFFSET * 2F)
    }

    private fun addLineOfHorizontalFlowers(
        gameSessionData: GameSessionData,
        services: Services,
        z: Float
    ) {
        val randomNumberOfFlowers =
            MathUtils.random(gameSessionData.tiles.size / 3 * 2, gameSessionData.tiles.size)
        for (i in 0 until randomNumberOfFlowers) {
            val position =
                auxVector.set(
                    MathUtils.random(-1F, gameSessionData.tiles.size.toFloat() + 1F),
                    0F,
                    z
                )
            addFlowers(services, position)
        }
    }

    private fun addLineOfVerticalFlowers(
        gameSessionData: GameSessionData,
        services: Services,
        x: Float
    ) {
        val randomNumberOfFlowers =
            MathUtils.random(gameSessionData.tiles.size / 3 * 2, gameSessionData.tiles.size)
        for (i in 0 until randomNumberOfFlowers) {
            val position =
                auxVector.set(
                    x,
                    0F,
                    MathUtils.random(-1F, gameSessionData.tiles.size.toFloat() + 1F)
                )
            addFlowers(services, position)
        }
    }

    private fun addFlowers(
        services: Services,
        position: Vector3
    ) {
        val modelInstance =
            ModelInstance(services.assetsManager.getAssetByDefinition(ModelsDefinitions.FLOWERS))
        modelInstance.transform.rotate(
            Vector3.Y,
            (MathUtils.random() * 4F).toInt().toFloat() * 90F
        )
        EntityBuilder.beginBuildingEntity(engine).addModelInstanceComponent(
            modelInstance,
            position,
            services.assetsManager.getCachedBoundingBox(ModelsDefinitions.FLOWERS)
        ).finishAndAddToEngine()
    }

    private fun createHorizontalLineOfTrees(
        z: Float
    ) {
        for (x in -4..(gameSessionData.tiles.size + 4) step 2) {
            addBackgroundTrees(services, auxVector.set(x.toFloat(), 0F, z))
        }
    }

    private fun createVerticalLineOfTrees(
        x: Float
    ) {
        for (z in -4..(gameSessionData.tiles.size + 4) step 2) {
            val position = auxVector.set(x, 0F, z.toFloat())
            addBackgroundTrees(services, position)
        }
    }

    private fun addBackgroundTrees(
        services: Services,
        position: Vector3
    ) {
        val modelInstance =
            ModelInstance(services.assetsManager.getAssetByDefinition(ModelsDefinitions.TREE_MERGED))
        modelInstance.transform.rotate(
            Vector3.Y,
            (MathUtils.random() * 4F).toInt().toFloat() * 90F
        )
            .scale(2F, 2F, 2F)
        EntityBuilder.beginBuildingEntity(engine).addModelInstanceComponent(
            modelInstance,
            position,
            services.assetsManager.getCachedBoundingBox(ModelsDefinitions.TREE_MERGED)
        ).finishAndAddToEngine()
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

    override val subscribedEvents: Map<SystemEvents, HandlerOnEvent>
        get() = mapOf(
            SystemEvents.PLAYER_LANDED to MapSystemOnPlayerLanded(this),
            SystemEvents.PLAYER_BEGIN to MapSystemOnPlayerBegin(this),
            SystemEvents.PLAYER_BLOWN to MapSystemOnPlayerBlown(),
            SystemEvents.POWERUP_ACTIVATED to MapSystemOnPowerupActivated(this),
        )


    override fun onSystemReady() {
        val offset = gameSessionData.tiles.size.toFloat() / 2F - BACKGROUND_GROUND_SIZE / 2F
        EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(
                ModelInstance(backgroundGroundModel),
                auxVector.set(
                    offset,
                    -0.01F,
                    offset
                ),
                backgroundGroundModel.calculateBoundingBox(auxBoundingBox)
            ).finishAndAddToEngine()
    }

    override fun dispose() {
        lineGrid.dispose()
        tileModel.dispose()
        backgroundGroundModel.dispose()
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
        private const val TREES_OFFSET = 4F
        private const val BACKGROUND_GROUND_SIZE: Float = 40F
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
        private val auxBoundingBox = BoundingBox()
    }

}
