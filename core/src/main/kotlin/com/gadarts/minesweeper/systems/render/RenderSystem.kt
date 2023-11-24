package com.gadarts.minesweeper.systems.render

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.utils.ScreenUtils
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.ModelInstanceComponent
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.SystemsGlobalData


class RenderSystem : GameEntitySystem() {

    private lateinit var axisModelHandler: AxisModelHandler
    private lateinit var modelBatch: ModelBatch
    private lateinit var modelEntities: ImmutableArray<Entity>

    override fun createGlobalData(systemsGlobalData: SystemsGlobalData) {
        super.createGlobalData(systemsGlobalData)
        axisModelHandler = AxisModelHandler()
        axisModelHandler.addAxis(engine)
        modelEntities = engine.getEntitiesFor(Family.all(ModelInstanceComponent::class.java).get())
        modelBatch = ModelBatch()
    }

    override fun onGlobalDataReady() {
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        ScreenUtils.clear(Color.BLACK, true)
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
            or GL20.GL_DEPTH_BUFFER_BIT
            or if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0)
        modelBatch.begin(globalData.camera)
        for (i in 0 until modelEntities.size()) {
            modelBatch.render(
                ComponentsMappers.modelInstance.get(modelEntities.get(i)).modelInstance
            )
        }
        modelBatch.end()
    }

    override fun dispose() {
        axisModelHandler.dispose()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        return false
    }

}
