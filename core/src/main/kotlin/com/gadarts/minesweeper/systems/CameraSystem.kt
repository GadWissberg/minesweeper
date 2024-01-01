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
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.data.SystemsGlobalData
import com.gadarts.minesweeper.systems.data.SystemsGlobalData.Companion.TEMP_GROUND_SIZE
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
            SystemEvents.PLAYER_BEGIN
        )
    }

    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager, soundPlayer)
        val cam = PerspectiveCamera(
            67F,
            MineSweeper.PORTRAIT_RESOLUTION_WIDTH.toFloat(),
            MineSweeper.PORTRAIT_RESOLUTION_HEIGHT.toFloat()
        )
        cam.near = NEAR
        cam.far = FAR
        cam.update()
        cam.position.set(TEMP_GROUND_SIZE / 2F, 16f, 10f)
        cam.lookAt(TEMP_GROUND_SIZE / 2F, 0f, 0f)
        originalCameraPosition.set(cam.position)
        systemsGlobalData.camera = cam
        if (GameDebugSettings.CAMERA_CONTROLLER_ENABLED) {
            cameraInputController = CameraInputController(cam)
            Gdx.input.inputProcessor = cameraInputController
        }

    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (!shakeCameraOffset.isZero && nextShake < TimeUtils.millis()) {
            val right = Vector3(globalData.camera.direction).crs(globalData.camera.up).nor()
            val xOffset =
                shakeCameraOffset.x * right.x + shakeCameraOffset.y * globalData.camera.up.x
            val yOffset =
                shakeCameraOffset.x * right.y + shakeCameraOffset.y * globalData.camera.up.y
            val zOffset =
                shakeCameraOffset.x * right.z + shakeCameraOffset.y * globalData.camera.up.z

            globalData.camera.position.add(
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
        } else {
            moveCamera(deltaTime)
        }
        cameraInputController?.update()
        globalData.camera.update()
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
        if (!originalCameraPosition.isZero) {
            val playerPosition =
                ComponentsMappers.modelInstance.get(globalData.player).modelInstance.transform.getTranslation(
                    auxVector
                )
            globalData.camera.position.x = Interpolation.exp5.apply(
                originalCameraPosition.x,
                playerPosition.x,
                cameraMovementProgress
            )
            globalData.camera.position.z = Interpolation.exp5.apply(
                originalCameraPosition.z,
                playerPosition.z + 8F,
                cameraMovementProgress
            )
            if (cameraMovementProgress >= 1F) {
                originalCameraPosition.setZero()
            } else {
                cameraMovementProgress += 0.4F * deltaTime
            }
        }
    }

    override fun onGlobalDataReady() {
    }

    override fun dispose() {
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.PLAYER_INITIATED_MOVE.ordinal || msg.message == SystemEvents.PLAYER_BEGIN.ordinal) {
            originalCameraPosition.set(globalData.camera.position)
            cameraMovementProgress = 0F
        } else if (msg.message == SystemEvents.MINE_TRIGGERED.ordinal) {
            shakeCameraOffset.set(
                MathUtils.random(SHAKE_MAX_OFFSET),
                MathUtils.random(SHAKE_MAX_OFFSET),
            )
            nextShake = TimeUtils.millis() + SHAKE_INTERVALS
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

    companion object {
        private const val FAR = 100f
        private const val NEAR = 0.01f
        private val auxVector = Vector3()
        private const val SHAKE_REDUCE_STEP_SIZE = 0.05F
        private const val SHAKE_INTERVALS = 100L
        private const val SHAKE_MAX_OFFSET = 0.3F
    }

}
