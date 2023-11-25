package com.gadarts.minesweeper.systems

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.EntityBuilder


class MapSystem : GameEntitySystem() {

    private lateinit var lineGrid: Model
    private lateinit var tempGround: Model

    override fun createGlobalData(systemsGlobalData: SystemsGlobalData) {
        super.createGlobalData(systemsGlobalData)
        val modelBuilder = ModelBuilder()
        createTempGround(modelBuilder)
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

    private fun createTempGround(modelBuilder: ModelBuilder) {
        tempGround = modelBuilder.createRect(
            0f, 0f, 0f,
            0f, 0f, TEMP_GROUND_SIZE,
            TEMP_GROUND_SIZE, 0f, TEMP_GROUND_SIZE,
            TEMP_GROUND_SIZE, 0f, 0f,
            0f, TEMP_GROUND_SIZE, 0f,
            Material(ColorAttribute.createDiffuse(Color.BROWN)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(ModelInstance(tempGround))
            .finishAndAddToEngine()
    }

    override fun onGlobalDataReady() {
    }

    override fun dispose() {
        tempGround.dispose()
        lineGrid.dispose()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        return false
    }

    companion object {
        const val TEMP_GROUND_SIZE = 10f
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
