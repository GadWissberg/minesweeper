package com.gadarts.minesweeper

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape
import com.gadarts.minesweeper.components.BaseParticleEffectComponent
import com.gadarts.minesweeper.components.CrateComponent
import com.gadarts.minesweeper.components.FollowerParticleEffectComponent
import com.gadarts.minesweeper.components.GroundComponent
import com.gadarts.minesweeper.components.IndependentParticleEffectComponent
import com.gadarts.minesweeper.components.ModelInstanceComponent
import com.gadarts.minesweeper.components.PhysicsComponent
import com.gadarts.minesweeper.components.PlayerComponent
import com.gadarts.minesweeper.components.TileComponent


class EntityBuilder {


    private var currentEntity: Entity? = null
    private var engine: PooledEngine? = null

    private fun init(engine: PooledEngine) {
        this.engine = engine
        this.currentEntity = engine.createEntity()
    }

    fun finishAndAddToEngine(): Entity {
        engine!!.addEntity(currentEntity)
        val entity = currentEntity
        finish()
        return entity!!
    }

    private fun finish() {
        instance.reset()
    }

    private fun reset() {
        engine = null
        currentEntity = null
    }

    fun addModelInstanceComponent(
        modelInstance: ModelInstance,
        boundingBox: BoundingBox
    ): EntityBuilder {
        return addModelInstanceComponent(modelInstance, Vector3.Zero, boundingBox)
    }

    fun addModelInstanceComponent(
        modelInstance: ModelInstance,
        position: Vector3,
        boundingBox: BoundingBox,
        manualRendering: Boolean = false
    ): EntityBuilder {
        val component: ModelInstanceComponent =
            engine!!.createComponent(ModelInstanceComponent::class.java)
        if (!position.isZero) {
            modelInstance.transform.setTranslation(position)
        }
        component.init(
            modelInstance,
            boundingBox,
            manualRendering,
        )
        currentEntity!!.add(component)
        component.modelInstance.userData = currentEntity
        return instance
    }

    fun addCrateComponent(): EntityBuilder {
        val component: CrateComponent =
            engine!!.createComponent(CrateComponent::class.java)
        currentEntity!!.add(component)
        return instance
    }

    fun addPlayerComponent(): EntityBuilder {
        val component: PlayerComponent = engine!!.createComponent(PlayerComponent::class.java)
        currentEntity!!.add(component)
        return instance
    }


    fun addTileComponent(): EntityBuilder {
        val component: TileComponent = engine!!.createComponent(TileComponent::class.java)
        currentEntity!!.add(component)
        return instance
    }

    fun addParticleEffectComponent(
        originalEffect: ParticleEffect,
        position: Vector3
    ): EntityBuilder {
        val particleEffectComponent = createIndependentParticleEffectComponent(
            originalEffect, position,
            engine!!
        )
        currentEntity!!.add(particleEffectComponent)
        return instance
    }

    fun addGroundComponent(): EntityBuilder {
        val component: GroundComponent = engine!!.createComponent(GroundComponent::class.java)
        currentEntity!!.add(component)
        return instance
    }


    companion object {
        private val instance = EntityBuilder()

        fun beginBuildingEntity(engine: Engine): EntityBuilder {
            instance.init(engine as PooledEngine)
            return instance
        }

        fun createPhysicsComponent(
            boundingBox: BoundingBox,
            transform: Matrix4,
            engine: PooledEngine
        ): PhysicsComponent {
            val component: PhysicsComponent = engine.createComponent(PhysicsComponent::class.java)
            val shape = btCompoundShape()
            val colShape = btBoxShape(
                Vector3(
                    boundingBox.width / 2F,
                    boundingBox.height / 2F,
                    boundingBox.depth / 2F
                )
            )
            shape.addChildShape(
                Matrix4().translate(Vector3(0F, boundingBox.height / 2F, 0F)),
                colShape
            )
            component.init(
                shape, 10F, transform, CF_CHARACTER_OBJECT
            )
            return component
        }

        fun createIndependentParticleEffectComponent(
            originalEffect: ParticleEffect,
            position: Vector3,
            engine: PooledEngine
        ): BaseParticleEffectComponent {
            val effect: ParticleEffect = originalEffect.copy()
            val particleEffectComponent = engine.createComponent(
                IndependentParticleEffectComponent::class.java
            )
            particleEffectComponent.init(effect)
            effect.translate(position)
            return particleEffectComponent
        }

        fun createFollowerParticleEffectComponent(
            originalEffect: ParticleEffect,
            engine: PooledEngine
        ): BaseParticleEffectComponent {
            val effect: ParticleEffect = originalEffect.copy()
            val particleEffectComponent = engine.createComponent(
                FollowerParticleEffectComponent::class.java
            )
            particleEffectComponent.init(effect)
            return particleEffectComponent
        }

    }
}
