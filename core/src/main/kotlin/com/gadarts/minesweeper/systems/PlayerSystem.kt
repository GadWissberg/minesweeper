package com.gadarts.minesweeper.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.components.ComponentsMappers

class PlayerSystem : GameEntitySystem(), InputProcessor {

    private val previousTouchPoint: Vector2 = Vector2()
    private val direction: Vector3 = Vector3()
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
        direction.set(Vector3.Z)
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val floatX = screenX.toFloat()
        val floatY = screenY.toFloat()
        val subtractedVector = auxVector2_1.set(floatX, floatY).sub(previousTouchPoint)
        val closestDirection =
            findClosestDirection(subtractedVector)
        val position =
            ComponentsMappers.modelInstance
                .get(globalData.player)
                .modelInstance!!
                .transform
                .getTranslation(auxVector3_2)
        ComponentsMappers.modelInstance
            .get(globalData.player)
            .modelInstance!!
            .transform
            .setToRotation(auxVector3_1.set(0F, -1F, 0F), closestDirection.direction.angleDeg())
            .trn(position)
        previousTouchPoint.set(floatX, floatY)
        return true
    }

    private fun findClosestDirection(subtractedVector: Vector2): Directions {
        var closestDirection = Directions.SOUTH
        var closestDistance = Float.MAX_VALUE

        for (direction in Directions.values()) {
            val distance = subtractedVector.dst2(direction.direction)
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

    enum class Directions(val direction: Vector2) {
        NORTH(Vector2(0F, -1F)),
        NORTH_EAST(Vector2(1F, -1F)),
        EAST(Vector2(1F, 0F)),
        SOUTH_EAST(Vector2(1F, 1F)),
        SOUTH(Vector2(0F, 1F)),
        SOUTH_WEST(Vector2(-1F, 1F)),
        WEST(Vector2(-1F, 0F)),
        NORTH_WEST(Vector2(-1F, -1F));
    }

    companion object {
        private val auxVector2_1: Vector2 = Vector2()
        private val auxVector3_1: Vector3 = Vector3()
        private val auxVector3_2: Vector3 = Vector3()

    }
}
