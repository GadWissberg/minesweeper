package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.ModelsDefinitions
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.SystemsGlobalData.Companion.TEMP_GROUND_SIZE
import kotlin.math.max
import kotlin.math.min


class MapSystem : GameEntitySystem() {

    private lateinit var tileModel: Model
    private var tiles: Array<Array<Entity?>> = Array(TEMP_GROUND_SIZE) {
        arrayOfNulls<Entity?>(TEMP_GROUND_SIZE)
    }
    private lateinit var lineGrid: Model

    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager)
        val modelBuilder = ModelBuilder()
        val modelInstance = ModelInstance(assetsManager.getAssetByDefinition(ModelsDefinitions.PIG))
        modelInstance.transform.setTranslation(
            TEMP_GROUND_SIZE / 2F + 0.5F, 0F, 0.5F
        ).rotate(Vector3.Y, -90F)
        val blendingAttribute = BlendingAttribute()
        blendingAttribute.opacity = 0.8F
        modelInstance.materials.get(0).set(blendingAttribute)
        globalData.player = EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(modelInstance)
            .addPlayerComponent()
            .finishAndAddToEngine()
        createTileModel(modelBuilder, assetsManager)
        for (row in values.indices) {
            val currentRow = row.toFloat()
            for (col in values[row].indices) {
                val tileModelInstance = ModelInstance(tileModel)
                val tileEntity = EntityBuilder.beginBuildingEntity(engine)
                    .addModelInstanceComponent(
                        tileModelInstance,
                        auxVector.set(col.toFloat(), 0F, currentRow)
                    ).finishAndAddToEngine()
                tiles[row][col] = tileEntity
            }
        }
    }

    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(SystemEvents.PLAYER_LANDED)
    }

    private fun createTileModel(
        modelBuilder: ModelBuilder,
        assetsManager: GameAssetManager
    ) {
        val diffuse = TextureAttribute.createDiffuse(
            assetsManager.getAssetByDefinition(
                TexturesDefinitions.TILE_UNREVEALED
            )
        )
        diffuse.scaleU = 1F
        diffuse.scaleV = 1F
        tileModel = modelBuilder.createRect(
            0F, 0F, 1F,
            1F, 0F, 1F,
            1F, 0F, 0F,
            0F, 0F, 0F,
            0F, 1F, 0F,
            Material(
                diffuse
            ),
            (Usage.Position or Usage.Normal or Usage.TextureCoordinates).toLong()
        )
    }

    override fun onGlobalDataReady() {
    }

    override fun dispose() {
        lineGrid.dispose()
        tileModel.dispose()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.PLAYER_LANDED.ordinal) {
            val position =
                ComponentsMappers.modelInstance.get(globalData.player).modelInstance.transform.getTranslation(
                    auxVector
                )
            val currentRow = position.z.toInt()
            val currentCol = position.x.toInt()
            var sum = 0
            for (row in max(currentRow - 1, 0)..min(currentRow + 1, tiles.size - 1)) {
                for (col in max(currentCol - 1, 0)..min(currentCol + 1, tiles[0].size - 1)) {
                    if (values[row][col] == 1 && (row != currentRow || col != currentCol)) {
                        sum += 1
                    }
                }
            }
            (ComponentsMappers.modelInstance.get(tiles[currentRow][currentCol]).modelInstance.materials[0].get(
                TextureAttribute.Diffuse
            ) as TextureAttribute).textureDescription.texture =
                assetsManger.getAssetByDefinition(sumToTextureDefinition[sum])
        }

        return false
    }

    companion object {
        private val auxVector = Vector3()
        private val values = arrayOf(
            arrayOf(0, 0, 0, 0, 2, 0, 0, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 0, 1, 1, 0, 0, 0),
            arrayOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 1, 0, 1, 0, 0),
            arrayOf(0, 0, 0, 1, 1, 0, 1, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 1, 0, 0, 1, 0),
            arrayOf(0, 0, 1, 0, 1, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 1, 0, 1, 0),
            arrayOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 3, 0, 0, 0, 0, 0),
        )
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
