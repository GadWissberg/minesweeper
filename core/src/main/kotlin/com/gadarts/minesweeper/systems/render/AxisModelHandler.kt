package com.gadarts.minesweeper.systems.render

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable
import com.gadarts.minesweeper.EntityBuilder


class AxisModelHandler : Disposable {
    private var axisModelX: Model? = null
    private var axisModelY: Model? = null
    private var axisModelZ: Model? = null
    fun addAxis(engine: Engine) {
        val modelBuilder = ModelBuilder()
        axisModelX = createAndAddAxisModel(
            engine as PooledEngine,
            modelBuilder,
            auxVector3_2.set(1f, 0f, 0f)
        )
        axisModelY = createAndAddAxisModel(engine, modelBuilder, auxVector3_2.set(0f, 1f, 0f))
        axisModelZ = createAndAddAxisModel(engine, modelBuilder, auxVector3_2.set(0f, 0f, 1f))
    }

    private fun createAndAddAxisModel(
        engine: PooledEngine,
        modelBuilder: ModelBuilder,
        vector: Vector3
    ): Model {
        val axisModel =
            createAxisModel(modelBuilder, vector, Color(vector.x, vector.y, vector.z, 1f))
        val axisModelInstanceX = ModelInstance(axisModel)
        EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(axisModelInstanceX)
            .finishAndAddToEngine()
        return axisModel
    }

    private fun createAxisModel(modelBuilder: ModelBuilder, dir: Vector3, color: Color): Model {
        return modelBuilder.createArrow(
            auxVector3_1.setZero(),
            dir,
            Material(ColorAttribute.createDiffuse(color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
    }

    override fun dispose() {
        axisModelX!!.dispose()
        axisModelY!!.dispose()
        axisModelZ!!.dispose()
    }

    companion object {
        private val auxVector3_1 = Vector3()
        private val auxVector3_2 = Vector3()
    }
}
