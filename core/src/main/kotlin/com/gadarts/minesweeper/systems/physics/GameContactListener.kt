package com.gadarts.minesweeper.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.physics.bullet.collision.ContactListener
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.SystemEvents

class GameContactListener(private val dispatcher: MessageDispatcher) : ContactListener() {

    override fun onContactAdded(
        colObj0: btCollisionObject?,
        partId0: Int,
        index0: Int,
        colObj1: btCollisionObject?,
        partId1: Int,
        index1: Int
    ): Boolean {
        if (colObj0 == null || colObj1 == null || colObj0.userData == null || colObj1.userData == null) return false

        var result = false
        if (isPlayerAndGround(colObj0, colObj1) && (checkHighVelocity(colObj0) || checkHighVelocity(
                colObj1
            ))
        ) {
            dispatcher.dispatchMessage(SystemEvents.PLAYER_PHYSICS_HARD_LAND.ordinal)
            result = true
        }
        return result
    }

    private fun checkHighVelocity(colObj0: btCollisionObject): Boolean {
        if (!ComponentsMappers.physics.has(colObj0.userData as Entity)) return false

        val rigidBody = ComponentsMappers.physics.get(colObj0.userData as Entity).rigidBody
        return rigidBody.linearVelocity.len2() > 50F
    }

    private fun isPlayerAndGround(
        colObj0: btCollisionObject,
        colObj1: btCollisionObject
    ): Boolean {
        return (ComponentsMappers.player.has(colObj0.userData as Entity) && ComponentsMappers.ground.has(
            colObj1.userData as Entity
        ))
            || (ComponentsMappers.ground.has(colObj0.userData as Entity) && ComponentsMappers.player.has(
            colObj1.userData as Entity
        ))
    }
}
