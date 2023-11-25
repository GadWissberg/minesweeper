package com.gadarts.minesweeper.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.gadarts.minesweeper.MineSweeper
import com.gadarts.minesweeper.systems.MapSystem.Companion.TEMP_GROUND_SIZE


class CameraSystem : GameEntitySystem(), InputProcessor {

    private lateinit var cameraInputController: CameraInputController

    override fun createGlobalData(systemsGlobalData: SystemsGlobalData) {
        super.createGlobalData(systemsGlobalData)
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
        systemsGlobalData.camera = cam
        cameraInputController = CameraInputController(cam)
        Gdx.input.inputProcessor = cameraInputController
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        cameraInputController.update()
        globalData.camera.update()
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
    }

}
