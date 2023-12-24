package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.ParticleEffectsDefinitions
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.components.BaseParticleEffectComponent
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.FollowerParticleEffectComponent
import com.gadarts.minesweeper.components.IndependentParticleEffectComponent


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
            Family.one(
                IndependentParticleEffectComponent::class.java,
                FollowerParticleEffectComponent::class.java
            ).get()
        )
        engine.addEntityListener(createEntityListener())
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

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.MINE_TRIGGERED.ordinal) {
            reactToMineTriggered()
            return true
        }

        return false
    }

    private fun handleCompletedParticleEffects() {
        particleEntitiesToRemove.clear()
        updateParticleEffectsComponents()
        removeParticleEffectsMarkedToBeRemoved()
    }

    private fun removeParticleEffectsMarkedToBeRemoved() {
        for (entity in particleEntitiesToRemove) {
            globalData.particleSystem.remove(fetchParticleEffect(entity).effect)
            entity.remove(BaseParticleEffectComponent::class.java)
        }
    }

    private fun updateParticleEffectsComponents() {
        for (entity in particleEffectsEntities) {
            val particleEffectComponent = fetchParticleEffect(entity)
            val particleEffect: ParticleEffect =
                particleEffectComponent.effect
            if (particleEffect.isComplete) {
                particleEntitiesToRemove.add(entity)
            } else if (ComponentsMappers.followersParticleEffect.has(entity)) {
                updateFollower(entity, particleEffectComponent)
            }
        }
    }

    private fun updateFollower(
        entity: Entity,
        particleEffectComponent: BaseParticleEffectComponent
    ) {
        ComponentsMappers.physics.get(entity).motionState?.getWorldTransform(
            auxMatrix
        )
        auxMatrix.setToTranslation(
            auxMatrix.`val`[Matrix4.M03],
            auxMatrix.`val`[Matrix4.M13],
            auxMatrix.`val`[Matrix4.M23]
        )
        particleEffectComponent.effect.setTransform(
            auxMatrix
        )
    }

    private fun fetchParticleEffect(entity: Entity): BaseParticleEffectComponent =
        if (ComponentsMappers.independentParticleEffect.has(entity)) ComponentsMappers.independentParticleEffect.get(
            entity
        ) else ComponentsMappers.followersParticleEffect.get(entity)

    private fun createEntityListener(): EntityListener {
        return object : EntityListener {
            override fun entityAdded(entity: Entity?) {
                if (ComponentsMappers.independentParticleEffect.has(entity)) {
                    val effect: ParticleEffect =
                        ComponentsMappers.independentParticleEffect.get(entity).effect
                    playParticleEffect(effect)
                }
            }

            override fun entityRemoved(entity: Entity?) {
            }
        }
    }


    private fun playParticleEffect(effect: ParticleEffect) {
        globalData.particleSystem.add(effect)
        effect.init()
        effect.start()
    }

    private fun updateSystem(deltaTime: Float) {
        globalData.particleSystem.update(deltaTime)
        globalData.particleSystem.begin()
        globalData.particleSystem.draw()
        globalData.particleSystem.end()
    }

    private fun reactToMineTriggered() {
        soundPlayer.playSoundByDefinition(SoundsDefinitions.EXPLOSION)
        EntityBuilder.beginBuildingEntity(engine).addParticleEffectComponent(
            assetsManger.getAssetByDefinition(
                ParticleEffectsDefinitions.EXPLOSION
            ),
            ComponentsMappers.modelInstance.get(globalData.player).modelInstance.transform.getTranslation(
                auxVector1
            ).add(0F, 0.1F, 0F)
        ).finishAndAddToEngine()
        val smokeParticleEffect = assetsManger.getAssetByDefinition(
            ParticleEffectsDefinitions.SMOKE
        )
        val followerParticleEffectComponent =
            EntityBuilder.createFollowerParticleEffectComponent(
                smokeParticleEffect, engine as PooledEngine
            )
        if (globalData.player != null) {
            globalData.player!!.add(
                followerParticleEffectComponent
            )
            playParticleEffect(followerParticleEffectComponent.effect)
            updateFollower(
                globalData.player!!,
                ComponentsMappers.followersParticleEffect.get(globalData.player)
            )
        }
    }

    companion object {
        val auxMatrix: Matrix4 = Matrix4()
        val auxVector1 = Vector3()
    }
}
