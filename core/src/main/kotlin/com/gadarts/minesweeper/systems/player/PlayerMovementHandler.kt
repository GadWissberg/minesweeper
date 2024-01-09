package com.gadarts.minesweeper.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.SystemsGlobalData
import kotlin.math.min

class PlayerMovementHandler {

    private var jumpProgress: Float = 0.0f
    private var desiredLocation: Vector3 = Vector3()
    private var originalLocation: Vector3 = Vector3()
    private var rotationDirection: Float = 0F
    private var currentDirection: Directions = Directions.SOUTH
    private var desiredDirection: Directions? = null
    fun reset() {
        jumpProgress = 0F
        desiredDirection = Directions.SOUTH
        originalLocation.setZero()
        rotationDirection = 0F
        currentDirection = Directions.SOUTH
        desiredLocation.setZero()
    }

    private fun findClosestDirection(subtractedVector: Vector2): Directions {
        var closestDirection = Directions.SOUTH
        var closestDistance = Float.MAX_VALUE

        for (direction in Directions.entries) {
            val distance = subtractedVector.dst2(direction.direction.x, direction.direction.z)
            if (distance < closestDistance) {
                closestDistance = distance
                closestDirection = direction
            }
        }

        return closestDirection
    }

    private fun updateMovement(deltaTime: Float, player: Entity, dispatcher: MessageDispatcher) {
        if (!desiredLocation.isZero && desiredDirection != null) {
            val x = desiredLocation.x.toInt()
            val z = desiredLocation.z.toInt()
            if (desiredLocation.x >= 0 && x < SystemsGlobalData.TEMP_GROUND_SIZE && desiredLocation.z >= 0 && z < SystemsGlobalData.TEMP_GROUND_SIZE) {
                val transform =
                    ComponentsMappers.modelInstance.get(player).modelInstance.transform
                transform.setTranslation(
                    auxVector3_1.set(originalLocation)
                        .slerp(desiredLocation, Interpolation.circle.apply(jumpProgress))
                )
                val currentPosition = transform.getTranslation(auxVector3_1)
                updateJumpHeight(currentPosition, dispatcher)
                jumpProgress += min(deltaTime * 4F, 0.1F)
                transform.setTranslation(currentPosition)
                if (transform.getTranslation(auxVector3_1)
                        .epsilonEquals(desiredLocation, 0.01F)
                ) {
                    desiredLocation.setZero()
                    jumpProgress = 0F
                }
            } else {
                desiredLocation.setZero()
            }
        }
    }

    private fun updateJumpHeight(currentPosition: Vector3, dispatcher: MessageDispatcher) {
        if (jumpProgress <= 0.5F) {
            currentPosition.y =
                Interpolation.bounceIn.apply(0F, JUMP_MAX_HEIGHT, jumpProgress)
        } else {
            currentPosition.y =
                Interpolation.bounceOut.apply(JUMP_MAX_HEIGHT, 0F, jumpProgress)
            if (jumpProgress >= 1F) {
                dispatcher.dispatchMessage(SystemEvents.PLAYER_LANDED.ordinal)
            }
        }
    }

    private fun rotate(player: Entity) {
        if (rotationDirection == 0F || desiredDirection == null) return
        val modelInstanceComponent = ComponentsMappers.modelInstance.get(player)
        var currentYaw = modelInstanceComponent
            .modelInstance
            .transform.getRotation(auxQuat.idt().nor())
            .yaw
        currentYaw = if (currentYaw < 0) currentYaw + 360 else currentYaw
        if (MathUtils.isEqual(currentYaw, desiredDirection!!.yaw, ROTATION_STEP)) {
            rotationDirection = 0F
            val position =
                modelInstanceComponent.modelInstance.transform.getTranslation(auxVector3_1)
            modelInstanceComponent.modelInstance.transform.setToRotation(
                Vector3.Y,
                desiredDirection!!.yaw
            ).trn(position)
            currentDirection = desiredDirection as Directions
            if (desiredLocation.isZero) {
                desiredDirection = null
            }
        } else {
            modelInstanceComponent.modelInstance.transform.rotate(
                Vector3.Y,
                rotationDirection
            )
        }
    }

    fun movePlayer(
        screenX: Int,
        screenY: Int,
        previousTouchPoint: Vector2,
        player: Entity,
        dispatcher: MessageDispatcher,
    ): Boolean {
        val current = auxVector2_1.set(screenX.toFloat(), screenY.toFloat())
        if (current.epsilonEquals(
                previousTouchPoint,
                0.1F
            ) || !desiredLocation.isZero
        ) return false
        val closestDirection = findClosestDirection(current.sub(previousTouchPoint).nor())
        desiredDirection = closestDirection
        previousTouchPoint.set(screenX.toFloat(), screenY.toFloat())
        var diff = currentDirection.yaw - desiredDirection!!.yaw
        diff += if (diff < 0) 360F else 0F
        rotationDirection = if (diff > 180) {
            ROTATION_STEP
        } else {
            -ROTATION_STEP
        }
        val originalLocation = ComponentsMappers.modelInstance
            .get(player)
            .modelInstance
            .transform.getTranslation(
                auxVector3_1
            )
        this.originalLocation.set(originalLocation)
        desiredLocation.set(
            originalLocation.add(desiredDirection!!.direction)
        )

        if (desiredLocation.x >= 0
            && desiredLocation.x < SystemsGlobalData.TEMP_GROUND_SIZE
            && desiredLocation.z >= 0
            && desiredLocation.z < SystemsGlobalData.TEMP_GROUND_SIZE
        ) {
            if (SystemsGlobalData.testMapValues[desiredLocation.z.toInt()][desiredLocation.x.toInt()] == 4) {
                desiredLocation.setZero()
            } else {
                dispatcher.dispatchMessage(SystemEvents.PLAYER_INITIATED_MOVE.ordinal)
            }
        }
        return true
    }

    fun update(deltaTime: Float, player: Entity, dispatcher: MessageDispatcher) {
        rotate(player)
        updateMovement(deltaTime, player, dispatcher)
    }

    private enum class Directions(val direction: Vector3) {


        NORTH(Vector3(0F, 0F, -1F)),
        EAST(Vector3(1F, 0F, 0F)),
        SOUTH(Vector3(0F, 0F, 1F)),
        WEST(Vector3(-1F, 0F, 0F));

        val yaw: Float

        init {
            direction.nor()
            var yaw = MathUtils.atan2(-direction.z, direction.x) * MathUtils.radiansToDegrees
            yaw = (yaw + 360) % 360
            this.yaw = yaw
        }
    }

    companion object {
        private val auxVector2_1 = Vector2()
        private val auxVector3_1 = Vector3()
        private const val ROTATION_STEP = 5F
        private val auxQuat: Quaternion = Quaternion()
        private const val JUMP_MAX_HEIGHT = 3F

    }
}
