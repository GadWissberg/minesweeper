package com.gadarts.minesweeper.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.GameDebugSettings
import com.gadarts.minesweeper.MineSweeper
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.SystemsGlobalData.Companion.TEMP_GROUND_SIZE


class CameraSystem : GameEntitySystem(), InputProcessor {

    private var cameraMovementProgress = 0F
    private var originalCameraPosition: Vector3 = Vector3()
    private var cameraInputController: CameraInputController? = null
    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(SystemEvents.PLAYER_MOVED)
    }

    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager)
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
        cameraInputController?.update()
        globalData.camera.update()
    }

    override fun onGlobalDataReady() {
    }

    override fun dispose() {
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.PLAYER_MOVED.ordinal) {
            originalCameraPosition.set(globalData.camera.position)
            cameraMovementProgress = 0F
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
    }

}
