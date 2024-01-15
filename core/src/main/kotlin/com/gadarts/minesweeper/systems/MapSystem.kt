package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Timer
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.ModelsDefinitions
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.TileComponent
import com.gadarts.minesweeper.systems.data.SystemsGlobalData
import com.gadarts.minesweeper.systems.data.SystemsGlobalData.Companion.TEMP_GROUND_SIZE
import kotlin.math.max
import kotlin.math.min


class MapSystem : GameEntitySystem() {

    private lateinit var tileModel: Model
    private var tiles: Array<Array<Entity?>> = Array(TEMP_GROUND_SIZE) {
        arrayOfNulls<Entity?>(TEMP_GROUND_SIZE)
    }
    private lateinit var lineGrid: Model

    override fun initialize(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer
    ) {
        super.initialize(systemsGlobalData, assetsManager, soundPlayer)
        tileModel = GameUtils.createTileModel(ModelBuilder(), assetsManager)
        for (row in SystemsGlobalData.testMapValues.indices) {
            for (col in SystemsGlobalData.testMapValues[row].indices) {
                val tileModelInstance = ModelInstance(tileModel)
                val tileEntity = EntityBuilder.beginBuildingEntity(engine)
                    .addModelInstanceComponent(
                        tileModelInstance,
                        auxVector.set(col.toFloat(), 0F, row.toFloat())
                    )
                    .addTileComponent()
                    .finishAndAddToEngine()
                tiles[row][col] = tileEntity
                if (SystemsGlobalData.testMapValues[row][col] == 3) {
                    (tileModelInstance.materials.get(0)
                        .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
                        assetsManger.getAssetByDefinition(TexturesDefinitions.TILE_DESTINATION)
                } else if (SystemsGlobalData.testMapValues[row][col] == 4) {
                    val modelInstance = ModelInstance(
                        assetsManager.getAssetByDefinition(ModelsDefinitions.ROCK)
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
        return listOf(SystemEvents.PLAYER_LANDED, SystemEvents.PLAYER_BEGIN)
    }


    override fun onSystemReady() {
    }

    override fun dispose() {
        lineGrid.dispose()
        tileModel.dispose()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.PLAYER_LANDED.ordinal) {
            handlePlayerLanded()
            return true
        } else if (msg.message == SystemEvents.PLAYER_BEGIN.ordinal) {
            val position =
                ComponentsMappers.modelInstance.get(globalData.playerData.player).modelInstance.transform.getTranslation(
                    auxVector
                )
            val currentRow = position.z.toInt()
            val currentCol = position.x.toInt()
            sumMinesAround(
                currentRow,
                currentCol
            )
            return true
        }

        return false
    }

    private fun handlePlayerLanded() {
        val position =
            ComponentsMappers.modelInstance.get(globalData.playerData.player).modelInstance.transform.getTranslation(
                auxVector
            )
        val currentRow = position.z.toInt()
        val currentCol = position.x.toInt()
        val currentValue = SystemsGlobalData.testMapValues[currentRow][currentCol]
        if (currentValue == 1 || currentValue == 3) {
            gameFinished(currentValue)
        } else {
            (ComponentsMappers.modelInstance.get(tiles[currentRow][currentCol]).modelInstance.materials[0].get(
                TextureAttribute.Diffuse
            ) as TextureAttribute).textureDescription.texture =
                assetsManger.getAssetByDefinition(
                    sumToTextureDefinition[sumMinesAround(
                        currentRow,
                        currentCol
                    )]
                )
        }
    }

    private fun sumMinesAround(currentRow: Int, currentCol: Int): Int {
        var sum = 0
        for (row in max(currentRow - 1, 0)..min(currentRow + 1, tiles.size - 1)) {
            for (col in max(currentCol - 1, 0)..min(currentCol + 1, tiles[0].size - 1)) {
                if (SystemsGlobalData.testMapValues[row][col] == 1 && (row != currentRow || col != currentCol)) {
                    sum += 1
                }
            }
        }
        dispatcher.dispatchMessage(SystemEvents.CURRENT_TILE_VALUE_CALCULATED.ordinal, sum)
        return sum
    }

    private fun gameFinished(currentValue: Int) {
        if (currentValue == 3) {
            soundPlayer.playSoundByDefinition(SoundsDefinitions.WIN)
        }
        dispatcher.dispatchMessage(SystemEvents.MINE_TRIGGERED.ordinal)
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                resetMap()
            }
        }, 5F)
    }

    private fun resetMap() {
        val tilesEntities = engine.getEntitiesFor(Family.all(TileComponent::class.java).get())
        val unrevealedTexture =
            assetsManger.getAssetByDefinition(TexturesDefinitions.TILE_UNREVEALED)
        val destinationTexture =
            assetsManger.getAssetByDefinition(TexturesDefinitions.TILE_DESTINATION)
        for (tile in tilesEntities) {
            val textureDescription =
                (ComponentsMappers.modelInstance.get(tile).modelInstance.materials.get(0)
                    .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription
            textureDescription.texture = if (textureDescription.texture != destinationTexture)
                unrevealedTexture else destinationTexture
        }
        dispatcher.dispatchMessage(SystemEvents.MAP_RESET.ordinal)
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
