package com.gadarts.minesweeper

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.components.ModelInstanceComponent
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

    companion object {
        private val instance = EntityBuilder()
        const val MSG_FAIL_CALL_BEGIN_BUILDING_ENTITY_FIRST = "Call beginBuildingEntity() first!"

        fun beginBuildingEntity(engine: Engine): EntityBuilder {
            instance.init(engine as PooledEngine)
            return instance
        }

    }
}
