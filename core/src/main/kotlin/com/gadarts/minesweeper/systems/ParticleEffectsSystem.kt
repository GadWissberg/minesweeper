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
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.assets.ParticleEffectsDefinitions
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.components.BaseParticleEffectComponent
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.FollowerParticleEffectComponent
import com.gadarts.minesweeper.components.IndependentParticleEffectComponent
import com.gadarts.minesweeper.systems.data.GameSessionData


class ParticleEffectsSystem : GameEntitySystem() {

    private lateinit var particleEffectsEntities: ImmutableArray<Entity>
    private lateinit var billboardParticleBatch: BillboardParticleBatch
    private val particleEntitiesToRemove = ArrayList<Entity>()

    override fun initialize(gameSessionData: GameSessionData, services: Services) {
        super.initialize(gameSessionData, services)
        this.gameSessionData.particleSystem = ParticleSystem()
        billboardParticleBatch = BillboardParticleBatch()
        services.assetsManager.loadParticleEffects(billboardParticleBatch)
        particleEffectsEntities = engine.getEntitiesFor(
            Family.one(
                IndependentParticleEffectComponent::class.java,
                FollowerParticleEffectComponent::class.java
            ).get()
        )
        engine.addEntityListener(createEntityListener())
    }

    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(SystemEvents.MINE_TRIGGERED, SystemEvents.PLAYER_BLOWN)
    }

    override fun onSystemReady() {
        billboardParticleBatch.setCamera(gameSessionData.camera)
        gameSessionData.particleSystem.add(billboardParticleBatch)
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
            gameSessionData.particleSystem.remove(fetchParticleEffect(entity).effect)
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
        ComponentsMappers.modelInstance.get(entity).modelInstance.transform.getTranslation(
            auxVector1
        )
        auxMatrix.setToTranslation(auxVector1)
        particleEffectComponent.effect.setTransform(auxMatrix)
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
        gameSessionData.particleSystem.add(effect)
        effect.init()
        effect.start()
    }

    private fun updateSystem(deltaTime: Float) {
        gameSessionData.particleSystem.update(deltaTime)
        gameSessionData.particleSystem.begin()
        gameSessionData.particleSystem.draw()
        gameSessionData.particleSystem.end()
    }

    private fun reactToMineTriggered() {
        services.soundPlayer.playSoundByDefinition(SoundsDefinitions.EXPLOSION)
        EntityBuilder.beginBuildingEntity(engine).addParticleEffectComponent(
            services.assetsManager.getAssetByDefinition(
                ParticleEffectsDefinitions.EXPLOSION
            ),
            ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.transform.getTranslation(
                auxVector1
            ).add(0F, 0.1F, 0F)
        ).finishAndAddToEngine()
        val smokeParticleEffect = services.assetsManager.getAssetByDefinition(
            ParticleEffectsDefinitions.SMOKE
        )
        val followerParticleEffectComponent =
            EntityBuilder.createFollowerParticleEffectComponent(
                smokeParticleEffect, engine as PooledEngine
            )
        if (gameSessionData.playerData.player != null) {
            gameSessionData.playerData.player!!.add(
                followerParticleEffectComponent
            )
            playParticleEffect(followerParticleEffectComponent.effect)
            updateFollower(
                gameSessionData.playerData.player!!,
                ComponentsMappers.followersParticleEffect.get(gameSessionData.playerData.player)
            )
        }
    }

    companion object {
        val auxMatrix: Matrix4 = Matrix4()
        val auxVector1 = Vector3()
    }
}
