package com.gadarts.minesweeper.systems

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.gadarts.minesweeper.EntityBuilder


class MapSystem : GameEntitySystem() {

    private lateinit var tempGround: Model

    override fun createGlobalData(systemsGlobalData: SystemsGlobalData) {
        super.createGlobalData(systemsGlobalData)
        val modelBuilder = ModelBuilder()
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
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        return false
    }

    companion object {
        const val TEMP_GROUND_SIZE = 100f

    }

}
