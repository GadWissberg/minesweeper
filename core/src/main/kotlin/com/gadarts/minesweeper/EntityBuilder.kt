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
import com.gadarts.minesweeper.components.ModelInstanceComponent
import com.gadarts.minesweeper.components.ParticleEffectComponent
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
        if (currentEntity == null) throw RuntimeException(MSG_FAIL_CALL_BEGIN_BUILDING_ENTITY_FIRST)
        engine!!.addEntity(currentEntity)
        val entity = currentEntity
        finish()
        return entity!!
    }

    private fun finish() {
        if (currentEntity == null) throw java.lang.RuntimeException(
            MSG_FAIL_CALL_BEGIN_BUILDING_ENTITY_FIRST
        )
        instance.reset()
    }

    private fun reset() {
        engine = null
        currentEntity = null
    }

    fun addModelInstanceComponent(modelInstance: ModelInstance): EntityBuilder {
        return addModelInstanceComponent(modelInstance, Vector3.Zero)
    }

    fun addModelInstanceComponent(modelInstance: ModelInstance, position: Vector3): EntityBuilder {
        if (currentEntity == null) throw RuntimeException(MSG_FAIL_CALL_BEGIN_BUILDING_ENTITY_FIRST)
        val component: ModelInstanceComponent =
            engine!!.createComponent(ModelInstanceComponent::class.java)
        if (!position.isZero) {
            modelInstance.transform.setTranslation(position)
        }
        component.init(modelInstance)
        currentEntity!!.add(component)
        component.modelInstance.userData = currentEntity
        return instance
    }

    fun addPlayerComponent(): EntityBuilder {
        if (currentEntity == null) throw RuntimeException(MSG_FAIL_CALL_BEGIN_BUILDING_ENTITY_FIRST)
        val component: PlayerComponent = engine!!.createComponent(PlayerComponent::class.java)
        currentEntity!!.add(component)
        return instance
    }


    fun addTileComponent(): EntityBuilder {
        if (currentEntity == null) throw RuntimeException(MSG_FAIL_CALL_BEGIN_BUILDING_ENTITY_FIRST)
        val component: TileComponent = engine!!.createComponent(TileComponent::class.java)
        currentEntity!!.add(component)
        return instance
    }

    fun addParticleEffectComponent(
        originalEffect: ParticleEffect,
        position: Vector3
    ): EntityBuilder {
        if (currentEntity == null) throw RuntimeException(MSG_FAIL_CALL_BEGIN_BUILDING_ENTITY_FIRST)
        val effect: ParticleEffect = originalEffect.copy()
        val particleEffectComponent = engine!!.createComponent(
            ParticleEffectComponent::class.java
        )
        particleEffectComponent.init(effect)
        effect.translate(position)
        currentEntity!!.add(particleEffectComponent)
        return instance
    }

    companion object {
        private val instance = EntityBuilder()
        const val MSG_FAIL_CALL_BEGIN_BUILDING_ENTITY_FIRST = "Call beginBuildingEntity() first!"

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

    }
}
