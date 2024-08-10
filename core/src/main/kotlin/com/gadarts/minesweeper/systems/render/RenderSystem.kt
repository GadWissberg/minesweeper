package com.gadarts.minesweeper.systems.render

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.ScreenUtils
import com.gadarts.minesweeper.GameDebugSettings
import com.gadarts.minesweeper.GameDebugSettings.DISABLE_FRUSTUM_CULLING
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.ModelInstanceComponent
import com.gadarts.minesweeper.components.ShrinkAnimationComponent
import com.gadarts.minesweeper.systems.CollisionShapesDebugDrawing
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData


class RenderSystem : GameEntitySystem() {

    private val shrinkAnimationEntities: ImmutableArray<Entity> by lazy {
        engine.getEntitiesFor(Family.all(ShrinkAnimationComponent::class.java).get())
    }
    private lateinit var shadowBatch: ModelBatch
    private lateinit var shadowLight: DirectionalShadowLight
    private lateinit var environment: Environment
    private lateinit var axisModelHandler: AxisModelHandler
    private lateinit var modelBatch: ModelBatch
    private lateinit var modelEntities: ImmutableArray<Entity>

    override fun initialize(gameSessionData: GameSessionData, managers: Managers) {
        super.initialize(gameSessionData, managers)
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

    override val subscribedEvents: Map<SystemEvents, HandlerOnEvent>
        get() = emptyMap()

    override fun onSystemReady() {
    }

    override fun update(deltaTime: Float) {
        shadowLight.begin(Vector3.Zero, gameSessionData.camera.direction)
        shadowBatch.begin(shadowLight.camera)
        renderModels(shadowBatch, false)
        shadowBatch.end()
        shadowLight.end()
        clearDisplay()
        modelBatch.begin(gameSessionData.camera)
        gameSessionData.numberOfVisible = 0
        renderModels(modelBatch, true)
        renderCollisionShapes()
        modelBatch.render(gameSessionData.particleSystem, environment)
        modelBatch.end()
        modelBatch.begin(gameSessionData.camera)
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
        renderModel(
            ComponentsMappers.modelInstance.get(gameSessionData.playerData.digit),
            false,
            modelBatch
        )
        modelBatch.end()
        for (entity in shrinkAnimationEntities) {
            updateShrinkAnimation(entity)
        }
    }

    private fun updateShrinkAnimation(entity: Entity) {
        val shrinkComponent = ComponentsMappers.shrinkAnimation.get(entity)
        val modelInstanceComponent = ComponentsMappers.modelInstance.get(entity)
        val animationProgress = shrinkComponent.animationProgress
        val transform = modelInstanceComponent.modelInstance.transform
        val initialTransform = shrinkComponent.getInitialTransform(auxMatrix)
        val originalPosition = initialTransform.getTranslation(auxVector3_1)
        val originalRotation = initialTransform.getRotation(
            auxQuat
        )
        val interpolation = shrinkComponent.interpolation
        transform.setToScaling(
            interpolation.apply(1F, shrinkComponent.scaleTarget.x, animationProgress),
            interpolation.apply(1F, shrinkComponent.scaleTarget.y, animationProgress),
            interpolation.apply(1F, shrinkComponent.scaleTarget.z, animationProgress)
        )
        transform.trn(
            originalPosition
        )
        transform.rotate(Vector3.X, originalRotation.pitch)
        transform.rotate(Vector3.Y, originalRotation.yaw)
        transform.rotate(Vector3.Z, originalRotation.roll)
        shrinkComponent.animationProgress += shrinkComponent.stepSize * if (shrinkComponent.shrink) -1F else 1F
        if (animationProgress >= 1F) {
            shrinkComponent.shrink = true
        } else if (animationProgress <= 0F) {
            shrinkComponent.shrink = false
        }
    }

    private fun clearDisplay() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        ScreenUtils.clear(Color.BLACK, true)
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
                    or GL20.GL_DEPTH_BUFFER_BIT
                    or if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0
        )
    }

    private fun renderCollisionShapes() {
        if (!GameDebugSettings.SHOW_COLLISION_SHAPES) return
        val debugDrawingMethod: CollisionShapesDebugDrawing? =
            gameSessionData.physicsData.debugDrawingMethod
        debugDrawingMethod?.drawCollisionShapes(gameSessionData.camera)
    }

    private fun renderModels(modelBatch: ModelBatch, applyEnvironment: Boolean) {
        for (i in 0 until modelEntities.size()) {
            val modelInstanceComponent = ComponentsMappers.modelInstance.get(modelEntities.get(i))
            if (!modelInstanceComponent.manualRendering) {
                renderModel(modelInstanceComponent, applyEnvironment, modelBatch)
            }
        }
    }

    private fun isInFrustum(
        modelInstanceComponent: ModelInstanceComponent
    ): Boolean {
        if (DISABLE_FRUSTUM_CULLING) return true

        val position = modelInstanceComponent.modelInstance.transform.getTranslation(auxVector3_1)
        val boundingBox: BoundingBox = modelInstanceComponent.getBoundingBox(auxBoundingBox)
        val center = boundingBox.getCenter(auxVector3_3)
        val dim: Vector3 = auxBoundingBox.getDimensions(auxVector3_2).scl(4.7F)
        return gameSessionData.camera.frustum.boundsInFrustum(position.add(center), dim)
    }

    private fun renderModel(
        modelInstanceComponent: ModelInstanceComponent,
        applyEnvironment: Boolean,
        modelBatch: ModelBatch
    ) {
        if (!isInFrustum(modelInstanceComponent)) return

        if (modelInstanceComponent.visible) {
            val modelInstance =
                modelInstanceComponent.modelInstance
            if (applyEnvironment) {
                modelBatch.render(modelInstance, environment)
            } else {
                modelBatch.render(modelInstance)
            }
            gameSessionData.numberOfVisible++
        }
    }

    override fun dispose() {
        axisModelHandler.dispose()
        modelBatch.dispose()
        shadowLight.dispose()
    }

    companion object {
        val ambientColor = Color(0.9F, 0.9F, 0.9F, 1F)
        private val auxVector3_1 = Vector3()
        private val auxVector3_2 = Vector3()
        private val auxVector3_3 = Vector3()
        private val auxBoundingBox = BoundingBox()
        private val auxMatrix = Matrix4()
        private val auxQuat = Quaternion()
    }
}
