package com.gadarts.minesweeper.systems.physics

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.PhysicsComponent
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData

class PhysicsSystem : GameEntitySystem() {
    private lateinit var contactListener: GameContactListener
    private lateinit var bulletEngineHandler: BulletEngineHandler

    override fun initialize(gameSessionData: GameSessionData, services: Services) {
        super.initialize(gameSessionData, services)
        bulletEngineHandler = BulletEngineHandler(this.gameSessionData)
        bulletEngineHandler.initialize(engine)
        contactListener = GameContactListener(services.dispatcher)
    }

    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(
            SystemEvents.PLAYER_BLOWN,
            SystemEvents.PLAYER_IS_ABOUT_TO_BE_REMOVED
        )
    }

    override fun dispose() {
        bulletEngineHandler.dispose()
        contactListener.dispose()
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        bulletEngineHandler.update(deltaTime)
    }

    override fun onSystemReady() {
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false
        if (msg.message == SystemEvents.PLAYER_BLOWN.ordinal) {
            val physicsComponent = EntityBuilder.createPhysicsComponent(
                ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.calculateBoundingBox(
                    auxBoundingBox
                ),
                ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.transform,
                engine as PooledEngine
            )
            gameSessionData.playerData.player?.add(physicsComponent)
            gameSessionData.physicsData.collisionWorld.addRigidBody(physicsComponent.rigidBody)
            physicsComponent.rigidBody.applyImpulse(
                Vector3(
                    MathUtils.random(-1F, 1F),
                    MathUtils.random(0.5F, 1F),
                    MathUtils.random(-1F, 1F)
                ).scl(95F),
                Vector3(
                    MathUtils.random(-0.1F, 0.1F),
                    0F,
                    MathUtils.random(-0.1F, 0.1F)
                )
            )
            physicsComponent.rigidBody.userData = gameSessionData.playerData.player
            return true
        } else if (msg.message == SystemEvents.PLAYER_IS_ABOUT_TO_BE_REMOVED.ordinal) {
            val player = gameSessionData.playerData.player
            if (ComponentsMappers.physics.has(player)) {
                val physicsComponent = ComponentsMappers.physics.get(
                    gameSessionData.playerData.player
                )
                gameSessionData.physicsData.collisionWorld.removeRigidBody(
                    physicsComponent.rigidBody
                )
                physicsComponent.dispose()
                gameSessionData.playerData.player?.remove(PhysicsComponent::class.java)
            }
        }
        return false
    }

    companion object {
        val auxBoundingBox = BoundingBox()
    }
}
