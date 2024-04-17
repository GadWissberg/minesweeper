package com.gadarts.minesweeper.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.TimeUtils
import com.gadarts.minesweeper.GameDebugSettings
import com.gadarts.minesweeper.MineSweeper
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.data.GameSessionData
import kotlin.math.absoluteValue


class CameraSystem : GameEntitySystem(), InputProcessor {

    private var nextShake: Long = 0L
    private var shakeCameraOffset = Vector2()
    private var cameraMovementProgress = 0F
    private var originalCameraPosition: Vector3 = Vector3()
    private var cameraInputController: CameraInputController? = null

    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(
            SystemEvents.PLAYER_INITIATED_MOVE,
            SystemEvents.MINE_TRIGGERED,
            SystemEvents.PLAYER_BEGIN,
            SystemEvents.PLAYER_BLOWN,
        )
    }

    override fun initialize(gameSessionData: GameSessionData, services: Services) {
        super.initialize(gameSessionData, services)
        val cam = PerspectiveCamera(
            67F,
            MineSweeper.PORTRAIT_RESOLUTION_WIDTH.toFloat(),
            MineSweeper.PORTRAIT_RESOLUTION_HEIGHT.toFloat()
        )
        cam.near = NEAR
        cam.far = FAR
        cam.update()
        originalCameraPosition.set(cam.position)
        gameSessionData.camera = cam
        if (GameDebugSettings.CAMERA_CONTROLLER_ENABLED) {
            cameraInputController = CameraInputController(cam)
            Gdx.input.inputProcessor = cameraInputController
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (!shakeCameraOffset.isZero && nextShake < TimeUtils.millis()) {
            handleCameraShakeEffect()
        } else {
            moveCamera(deltaTime)
        }
        cameraInputController?.update()
        gameSessionData.camera.update()
    }

    override fun onSystemReady() {
        val playerPosition =
            ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.transform.getTranslation(
                auxVector
            )
        gameSessionData.camera.position.set(
            playerPosition.x,
            18f,
            playerPosition.z + CAMERA_OFFSET_FROM_PLAYER
        )
        gameSessionData.camera.lookAt(playerPosition)
    }

    override fun dispose() {
    }


    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.PLAYER_INITIATED_MOVE.ordinal || msg.message == SystemEvents.PLAYER_BEGIN.ordinal) {
            originalCameraPosition.set(gameSessionData.camera.position)
            cameraMovementProgress = 0F
            return true
        } else if (msg.message == SystemEvents.PLAYER_BLOWN.ordinal) {
            shakeCameraOffset.set(
                MathUtils.random(SHAKE_MAX_OFFSET),
                MathUtils.random(SHAKE_MAX_OFFSET),
            )
            nextShake = TimeUtils.millis() + SHAKE_INTERVALS
            return true
        }
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
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false

    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false

    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false

    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false

    }

    private fun calculateShakeCameraOffsetCoordinate(coordinate: Float) =
        if (coordinate.absoluteValue > SHAKE_REDUCE_STEP_SIZE) coordinate * -1F + reduceShakeStepSize(
            coordinate
        ) else 0F

    private fun reduceShakeStepSize(currentValue: Float): Float {
        return if (currentValue.absoluteValue > SHAKE_REDUCE_STEP_SIZE) {
            if (currentValue > 0) {
                SHAKE_REDUCE_STEP_SIZE
            } else {
                -SHAKE_REDUCE_STEP_SIZE
            }
        } else 0F
    }

    private fun moveCamera(deltaTime: Float) {
        if (!originalCameraPosition.isZero && gameSessionData.playerData.player != null) {
            val playerPosition =
                ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.transform.getTranslation(
                    auxVector
                )
            gameSessionData.camera.position.x = Interpolation.exp5.apply(
                originalCameraPosition.x,
                playerPosition.x,
                cameraMovementProgress
            )
            gameSessionData.camera.position.z = Interpolation.exp5.apply(
                originalCameraPosition.z,
                playerPosition.z + CAMERA_OFFSET_FROM_PLAYER,
                cameraMovementProgress
            )
            if (cameraMovementProgress >= 1F) {
                originalCameraPosition.setZero()
            } else {
                cameraMovementProgress += 0.4F * deltaTime
            }
        }
    }

    private fun handleCameraShakeEffect() {
        val right = Vector3(gameSessionData.camera.direction).crs(gameSessionData.camera.up).nor()
        val xOffset =
            shakeCameraOffset.x * right.x + shakeCameraOffset.y * gameSessionData.camera.up.x
        val yOffset =
            shakeCameraOffset.x * right.y + shakeCameraOffset.y * gameSessionData.camera.up.y
        val zOffset =
            shakeCameraOffset.x * right.z + shakeCameraOffset.y * gameSessionData.camera.up.z
        gameSessionData.camera.position.add(
            xOffset,
            yOffset,
            zOffset
        )
        shakeCameraOffset.set(
            calculateShakeCameraOffsetCoordinate(shakeCameraOffset.x),
            calculateShakeCameraOffsetCoordinate(shakeCameraOffset.y),
        )
        nextShake = if (shakeCameraOffset.isZero) {
            0L
        } else {
            TimeUtils.millis() + SHAKE_INTERVALS
        }
    }

    companion object {
        private const val CAMERA_OFFSET_FROM_PLAYER = 4F
        private const val FAR = 100f
        private const val NEAR = 0.01f
        private val auxVector = Vector3()
        private const val SHAKE_REDUCE_STEP_SIZE = 0.05F
        private const val SHAKE_INTERVALS = 100L
        private const val SHAKE_MAX_OFFSET = 0.3F
    }
}
