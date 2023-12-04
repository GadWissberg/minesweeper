package com.gadarts.minesweeper.systems

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.ModelsDefinitions
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.systems.SystemsGlobalData.Companion.TEMP_GROUND_SIZE


class MapSystem : GameEntitySystem() {

    private lateinit var tileModel: Model
    private lateinit var lineGrid: Model

    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager)
        val modelBuilder = ModelBuilder()
        addGrid(modelBuilder)
        val modelInstance = ModelInstance(assetsManager.getAssetByDefinition(ModelsDefinitions.PIG))
        modelInstance.transform.setTranslation(
            TEMP_GROUND_SIZE / 2F + 0.5F, 0F, 0.5F
        ).rotate(Vector3.Y, -90F)
        globalData.player = EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(modelInstance)
            .addPlayerComponent()
            .finishAndAddToEngine()
        createTileModel(modelBuilder, assetsManager)
        for (row in values.indices) {
            val currentRow = row.toFloat()
            for (col in values[row].indices) {
                val tileModelInstance = ModelInstance(tileModel)
                EntityBuilder.beginBuildingEntity(engine)
                    .addModelInstanceComponent(
                        tileModelInstance,
                        auxVector.set(col.toFloat(), 0F, currentRow)
                    ).finishAndAddToEngine()
            }
        }
    }

    private fun createTileModel(
        modelBuilder: ModelBuilder,
        assetsManager: GameAssetManager
    ) {
        tileModel = modelBuilder.createRect(
            0F, 0F, 0F,
            0F, 0F, 1F,
            1F, 0F, 1F,
            1F, 0F, 0F,
            0F, 1F, 0F,
            Material(
                TextureAttribute.createDiffuse(
                    assetsManager.getAssetByDefinition(
                        TexturesDefinitions.TILE_0
                    )
                )
            ),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
    }

    private fun addGrid(modelBuilder: ModelBuilder) {
        val halfSize = TEMP_GROUND_SIZE / 2F
        lineGrid = modelBuilder.createLineGrid(
            TEMP_GROUND_SIZE.toInt(),
            TEMP_GROUND_SIZE.toInt(),
            1F,
            1F,
            Material(
                ColorAttribute.createDiffuse(Color.WHITE)
            ),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(ModelInstance(lineGrid), Vector3(halfSize, 0.1F, halfSize))
            .finishAndAddToEngine()
    }

    override fun onGlobalDataReady() {
    }

    override fun dispose() {
        lineGrid.dispose()
        tileModel.dispose()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        return false
    }

    companion object {
        private val auxVector = Vector3()
        private val values = arrayOf(
            arrayOf(0, 0, 0, 0, 2, 0, 0, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 0, 0, 1, 0, 0, 0),
            arrayOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 1, 0, 1, 0, 0),
            arrayOf(0, 0, 0, 1, 1, 0, 1, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 1, 0, 0, 1, 0),
            arrayOf(0, 0, 1, 0, 1, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 1, 0, 1, 0),
            arrayOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 3, 0, 0, 0, 0, 0),
        )

    }

}
