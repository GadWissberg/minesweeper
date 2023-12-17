package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.ParticleEffectsDefinitions
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.ParticleEffectComponent


class ParticleEffectsSystem : GameEntitySystem() {

    private lateinit var particleEffectsEntities: ImmutableArray<Entity>
    private lateinit var billboardParticleBatch: BillboardParticleBatch
    private val particleEntitiesToRemove = ArrayList<Entity>()
    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager, soundPlayer)
        globalData.particleSystem = ParticleSystem()
        billboardParticleBatch = BillboardParticleBatch()
        assetsManager.loadParticleEffects(billboardParticleBatch)
        particleEffectsEntities = engine.getEntitiesFor(
            Family.all(
                ParticleEffectComponent::class.java
            ).get()
        )
        engine.addEntityListener(createEntityListener())
    }

    private fun handleCompletedParticleEffects() {
        particleEntitiesToRemove.clear()
        addCompleteToRemove()
        removeParticleEffectsMarkedToBeRemoved()
    }

    private fun removeParticleEffectsMarkedToBeRemoved() {
        for (entity in particleEntitiesToRemove) {
            if (ComponentsMappers.particleEffect.has(entity)) {
                globalData.particleSystem.remove(
                    ComponentsMappers.particleEffect.get(entity).effect
                )
            }
            engine.removeEntity(entity)
        }
    }

    private fun addCompleteToRemove() {
        for (entity in particleEffectsEntities) {
            val particleEffect: ParticleEffect =
                ComponentsMappers.particleEffect.get(entity).effect
            if (particleEffect.isComplete) {
                particleEntitiesToRemove.add(entity)
            }
        }
    }

    private fun createEntityListener(): EntityListener {
        return object : EntityListener {
            override fun entityAdded(entity: Entity?) {
                if (ComponentsMappers.particleEffect.has(entity)) {
                    val effect: ParticleEffect =
                        ComponentsMappers.particleEffect.get(entity).effect
                    effect.init()
                    effect.start()
                    globalData.particleSystem.add(effect)
                }
            }

            override fun entityRemoved(entity: Entity?) {
            }
        }
    }

    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(SystemEvents.MINE_TRIGGERED)
    }

    override fun onGlobalDataReady() {
        billboardParticleBatch.setCamera(globalData.camera)
        globalData.particleSystem.add(billboardParticleBatch)
    }

    override fun dispose() {
    }

    override fun update(deltaTime: Float) {
        updateSystem(deltaTime)
        handleCompletedParticleEffects()
    }

    private fun updateSystem(deltaTime: Float) {
        globalData.particleSystem.update(deltaTime)
        globalData.particleSystem.begin()
        globalData.particleSystem.draw()
        globalData.particleSystem.end()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false
        if (msg.message == SystemEvents.MINE_TRIGGERED.ordinal) {
            soundPlayer.playSound(SoundsDefinitions.EXPLOSION)
            EntityBuilder.beginBuildingEntity(engine).addParticleEffectComponent(
                assetsManger.getAssetByDefinition(
                    ParticleEffectsDefinitions.EXPLOSION
                ),
                ComponentsMappers.modelInstance.get(globalData.player).modelInstance.transform.getTranslation(
                    auxVector1
                ).add(0F, 0.1F, 0F)
            ).finishAndAddToEngine()
            return true
        }
        return false
    }

    companion object {
        val auxVector1 = Vector3()
    }
}
