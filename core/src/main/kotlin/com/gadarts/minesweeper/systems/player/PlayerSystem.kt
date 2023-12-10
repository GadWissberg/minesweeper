package com.gadarts.minesweeper.systems.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.ModelsDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.SystemsGlobalData


class PlayerSystem : GameEntitySystem(), InputProcessor {

    private val previousTouchPoint: Vector2 = Vector2()
    private val playerMovementHandler = PlayerMovementHandler()

    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager)
        addPlayer()
        if (Gdx.input.inputProcessor == null) {
            Gdx.input.inputProcessor = this
        }
    }

    override fun onGlobalDataReady() {
    }

    override fun dispose() {
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.MAP_RESET.ordinal) {
            playerMovementHandler.reset()
            engine.removeEntity(globalData.player)
            globalData.player = null
            Gdx.app.postRunnable { addPlayer() }
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
        previousTouchPoint.set(screenX.toFloat(), screenY.toFloat())
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (globalData.player == null) return false

        playerMovementHandler.movePlayer(
            screenX,
            screenY,
            previousTouchPoint,
            globalData.player!!,
            dispatcher
        )

        return true
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(SystemEvents.MAP_RESET)
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

    override fun update(deltaTime: Float) {
        if (globalData.player == null) return
        playerMovementHandler.update(deltaTime, globalData.player!!, dispatcher)
    }


    private fun addPlayer() {
        val modelInstance = ModelInstance(assetsManger.getAssetByDefinition(ModelsDefinitions.PIG))
        val blendingAttribute = BlendingAttribute()
        blendingAttribute.opacity = 0.8F
        modelInstance.materials.get(0).set(blendingAttribute)
        globalData.player = EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(modelInstance)
            .addPlayerComponent()
            .finishAndAddToEngine()
        placePlayer()
    }

    private fun placePlayer() {
        for (row in SystemsGlobalData.values.indices) {
            for (col in SystemsGlobalData.values[0].indices) {
                if (SystemsGlobalData.values[row][col] == 2) {
                    ComponentsMappers.modelInstance.get(globalData.player).modelInstance.transform.setToTranslation(
                        col + 0.5F, 0F, row + 0.5F
                    ).rotate(Vector3.Y, -90F)
                    return
                }
            }
        }
    }


    companion object
}
