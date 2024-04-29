package com.gadarts.minesweeper.systems.physics.react

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.physics.PhysicsSystem

class PhysicsSystemOnPlayerBlown : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        services: Services
    ) {
        val physicsComponent = EntityBuilder.createPhysicsComponent(
            ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.calculateBoundingBox(
                PhysicsSystem.auxBoundingBox
            ),
            ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.transform,
            services.engine
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
    }
}