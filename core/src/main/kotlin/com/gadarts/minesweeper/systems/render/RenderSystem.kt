package com.gadarts.minesweeper.systems.render

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.ScreenUtils
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.ModelInstanceComponent
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.SystemsGlobalData


class RenderSystem : GameEntitySystem() {

    private lateinit var shadowBatch: ModelBatch
    private lateinit var shadowLight: DirectionalShadowLight
    private lateinit var environment: Environment
    private lateinit var axisModelHandler: AxisModelHandler
    private lateinit var modelBatch: ModelBatch
    private lateinit var modelEntities: ImmutableArray<Entity>

    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager)
        axisModelHandler = AxisModelHandler()
        axisModelHandler.addAxis(engine)
        modelEntities = engine.getEntitiesFor(Family.all(ModelInstanceComponent::class.java).get())
        modelBatch = ModelBatch()
        environment = Environment()
        environment.set(
            ColorAttribute(
                ColorAttribute.AmbientLight,
                ambientColor
            )
        )
        val dirValue = 0.4f
        shadowLight = DirectionalShadowLight(
            2056,
            2056,
            60f,
            60f,
            .1f,
            150f
        )
        shadowLight.set(dirValue, dirValue, dirValue, 40.0f, -35f, -35f)
        environment.add(shadowLight)
        environment.shadowMap = shadowLight
        shadowBatch = ModelBatch(DepthShaderProvider())
    }

    override fun onGlobalDataReady() {
    }

    override fun update(deltaTime: Float) {
        shadowLight.begin(Vector3.Zero, globalData.camera.direction)
        shadowBatch.begin(shadowLight.camera)
        renderModels(shadowBatch, false)
        shadowBatch.end()
        shadowLight.end()
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        ScreenUtils.clear(Color.BLACK, true)
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
                or GL20.GL_DEPTH_BUFFER_BIT
                or if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0
        )
        modelBatch.begin(globalData.camera)
        renderModels(modelBatch, true)
        modelBatch.render(globalData.particleSystem, environment)
        modelBatch.end()
    }

    private fun renderModels(modelBatch: ModelBatch, applyEnvironment: Boolean) {
        for (i in 0 until modelEntities.size()) {
            val modelInstance =
                ComponentsMappers.modelInstance.get(modelEntities.get(i)).modelInstance
            if (applyEnvironment) {
                modelBatch.render(modelInstance, environment)
            } else {
                modelBatch.render(modelInstance)
            }
        }
    }

    override fun dispose() {
        axisModelHandler.dispose()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        return false
    }

    companion object {
        val ambientColor = Color(0.9F, 0.9F, 0.9F, 1F)
    }
}
