package com.gadarts.minesweeper.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.SystemsGlobalData.Companion.TEMP_GROUND_SIZE
import kotlin.math.min


class PlayerSystem : GameEntitySystem(), InputProcessor {

    private var jumpProgress: Float = 0.0f
    private var desiredLocation: Vector3 = Vector3()
    private var originalLocation: Vector3 = Vector3()
    private var rotationDirection: Float = 0F
    private var currentDirection: Directions = Directions.SOUTH
    private var desiredDirection: Directions? = null
    private val previousTouchPoint: Vector2 = Vector2()

    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager)
        if (Gdx.input.inputProcessor == null) {
            Gdx.input.inputProcessor = this
        }
    }

    override fun onGlobalDataReady() {

    }

    override fun dispose() {
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        previousTouchPoint.set(screenX.toFloat(), screenY.toFloat())
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!desiredLocation.isZero || desiredDirection == null) return false

        var diff = currentDirection.yaw - desiredDirection!!.yaw
        if (diff < 0) {
            diff += 360F
        }
        rotationDirection = if (diff > 180) {
            ROTATION_STEP
        } else {
            -ROTATION_STEP
        }

        val originalLocation = ComponentsMappers.modelInstance
            .get(globalData.player)
            .modelInstance
            .transform.getTranslation(
                auxVector3_1
            )
        this.originalLocation.set(originalLocation)
        desiredLocation.set(
            originalLocation.add(desiredDirection!!.direction)
        )
        dispatcher.dispatchMessage(SystemEvents.PLAYER_INITIATED_MOVE.ordinal)
        return true
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun update(deltaTime: Float) {
        rotate()
        updateMovement(deltaTime)
    }

    private fun updateMovement(deltaTime: Float) {
        if (!desiredLocation.isZero && desiredDirection != null) {
            val x = desiredLocation.x.toInt()
            val z = desiredLocation.z.toInt()
            if (desiredLocation.x >= 0 && x < TEMP_GROUND_SIZE && desiredLocation.z >= 0 && z < TEMP_GROUND_SIZE) {
                val transform =
                    ComponentsMappers.modelInstance.get(globalData.player).modelInstance.transform
                transform.setTranslation(
                    auxVector3_1.set(originalLocation)
                        .slerp(desiredLocation, Interpolation.circle.apply(jumpProgress))
                )
                val currentPosition = transform.getTranslation(auxVector3_1)
                updateJumpHeight(currentPosition)
                jumpProgress += min(deltaTime * 2F, 0.1F)
                transform.setTranslation(currentPosition)
                if (transform.getTranslation(auxVector3_1).epsilonEquals(desiredLocation, 0.01F)) {
                    desiredLocation.setZero()
                    jumpProgress = 0F
                }
            } else {
                desiredLocation.setZero()
            }
        }
    }

    private fun updateJumpHeight(currentPosition: Vector3) {
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

    private fun rotate() {
        if (rotationDirection == 0F || desiredDirection == null) return
        val modelInstanceComponent = ComponentsMappers.modelInstance.get(globalData.player)
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

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val current = auxVector2_1.set(screenX.toFloat(), screenY.toFloat())
        if (current.epsilonEquals(previousTouchPoint, 0.1F) || !desiredLocation.isZero) return false

        val subtractedVector = current.sub(previousTouchPoint).nor()
        val closestDirection =
            findClosestDirection(subtractedVector)
        desiredDirection = closestDirection

        previousTouchPoint.set(screenX.toFloat(), screenY.toFloat())
        return true
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

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
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
        private val auxVector2_1: Vector2 = Vector2()
        private val auxVector3_1: Vector3 = Vector3()
        private val auxQuat: Quaternion = Quaternion()
        private const val ROTATION_STEP = 5F
        private const val JUMP_MAX_HEIGHT = 2F

    }
}
