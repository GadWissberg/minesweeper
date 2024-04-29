package com.gadarts.minesweeper.systems.physics.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.PhysicsComponent
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData

class PhysicsSystemOnPlayerIsAboutToBeRemoved : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        services: Services
    ) {
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
}