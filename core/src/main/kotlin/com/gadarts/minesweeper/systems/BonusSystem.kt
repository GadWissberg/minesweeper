package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.ModelsDefinitions
import com.gadarts.minesweeper.assets.ParticleEffectsDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.CrateComponent
import com.gadarts.minesweeper.systems.data.SystemsGlobalData

class BonusSystem : GameEntitySystem() {
    private lateinit var crates: ImmutableArray<Entity>

    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager, soundPlayer)
        crates = engine.getEntitiesFor(Family.all(CrateComponent::class.java).get())
        addCrates(assetsManager)
    }

    private fun addCrates(assetsManager: GameAssetManager) {
        for (row in SystemsGlobalData.testMapValues.indices) {
            for (col in SystemsGlobalData.testMapValues[0].indices) {
                if (SystemsGlobalData.testMapValues[row][col] == 5) {
                    val modelInstance = ModelInstance(
                        assetsManager.getAssetByDefinition(ModelsDefinitions.CRATE)
                    )
                    modelInstance.calculateBoundingBox(auxBoundingBox)
                    val entity = EntityBuilder.beginBuildingEntity(engine)
                        .addModelInstanceComponent(
                            modelInstance,
                            Vector3(col + 0.5F, auxBoundingBox.height / 2, row + 0.5F)
                        )
                        .addCrateComponent()
                        .finishAndAddToEngine()
                    globalData.mapData[row][col].crate = entity
                    return
                }
            }
        }
    }

    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(SystemEvents.PLAYER_LANDED)
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.PLAYER_LANDED.ordinal) {
            val position =
                ComponentsMappers.modelInstance.get(globalData.player).modelInstance.transform.getTranslation(
                    auxVector
                )
            val row = position.z.toInt()
            val col = position.x.toInt()
            if (SystemsGlobalData.testMapValues[row][col] == 5 && globalData.mapData[row][col].crate != null) {
                engine.removeEntity(globalData.mapData[row][col].crate)
                EntityBuilder.beginBuildingEntity(engine).addParticleEffectComponent(
                    assetsManger.getAssetByDefinition(ParticleEffectsDefinitions.CRATE_PARTICLES),
                    position
                ).finishAndAddToEngine()
            }
        }

        return false
    }

    override fun onGlobalDataReady() {
    }

    override fun dispose() {
    }

    override fun update(deltaTime: Float) {
        for (crate in crates) {
            updateCrateAnimation(crate)
        }
    }

    private fun updateCrateAnimation(crate: Entity?) {
        val crateComponent = ComponentsMappers.crate.get(crate)
        val modelInstanceComponent = ComponentsMappers.modelInstance.get(crate)
        val interpolation = Interpolation.bounce
        val animationProgress = crateComponent.animationProgress
        modelInstanceComponent.modelInstance.transform.values[Matrix4.M00] =
            interpolation.apply(1F, 1.1F, animationProgress)
        modelInstanceComponent.modelInstance.transform.values[Matrix4.M11] =
            interpolation.apply(1F, 0.8F, animationProgress)
        modelInstanceComponent.modelInstance.transform.values[Matrix4.M22] =
            interpolation.apply(1F, 1.1F, animationProgress)
        crateComponent.animationProgress += 0.01F * if (crateComponent.shrink) -1F else 1F
        if (animationProgress >= 1F) {
            crateComponent.shrink = true
        } else if (animationProgress <= 0F) {
            crateComponent.shrink = false
        }
    }

    companion object {
        val auxVector = Vector3()
        private val auxBoundingBox = BoundingBox()

    }
}
