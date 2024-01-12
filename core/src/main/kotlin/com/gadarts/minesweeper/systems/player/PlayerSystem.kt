package com.gadarts.minesweeper.systems.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.ModelsDefinitions
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.GameUtils
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.SystemsGlobalData


class PlayerSystem : GameEntitySystem(), InputProcessor {

    private lateinit var digitModel: Model
    private lateinit var regularJumpSound: Sound
    private lateinit var characterJumpSounds: List<Sound>
    private val previousTouchPoint: Vector2 = Vector2()
    private lateinit var playerMovementHandler: PlayerMovementHandler

    override fun initialize(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer
    ) {
        super.initialize(systemsGlobalData, assetsManager, soundPlayer)
        addPlayer()
        val modelBuilder = ModelBuilder()
        digitModel = GameUtils.createTileModel(modelBuilder, assetsManager, -0.5F)
        addDigit()
        playerMovementHandler = PlayerMovementHandler(globalData.digit)
        if (Gdx.input.inputProcessor == null) {
            Gdx.input.inputProcessor = this
        }
        characterJumpSounds = SoundsDefinitions.PIG_JUMP.getPaths()
            .map { path -> assetsManager.get<Sound>(path) }
            .toList()
        regularJumpSound = assetsManager.getAssetByDefinition(SoundsDefinitions.JUMP)
    }

    override fun onSystemReady() {
        dispatcher.dispatchMessage(SystemEvents.PLAYER_BEGIN.ordinal)
    }

    override fun dispose() {
        digitModel.dispose()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.MAP_RESET.ordinal) {
            playerMovementHandler.reset()
            engine.removeEntity(globalData.player)
            globalData.player = null
            Gdx.app.postRunnable {
                addPlayer()
                dispatcher.dispatchMessage(SystemEvents.PLAYER_BEGIN.ordinal)
            }
        } else if (msg.message == SystemEvents.PLAYER_PHYSICS_HARD_LAND.ordinal) {
            soundPlayer.playSoundByDefinition(SoundsDefinitions.TAP)
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
        if (globalData.player == null || ComponentsMappers.physics.has(globalData.player)) return false

        val moved = playerMovementHandler.movePlayer(
            screenX,
            screenY,
            previousTouchPoint,
            globalData.player!!,
            dispatcher,
        )
        if (moved) {
            val sound = if (MathUtils.random(5) == 0) {
                characterJumpSounds.random()
            } else {
                regularJumpSound
            }
            soundPlayer.playSound(sound, 0.5F)
        }
        return true
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(SystemEvents.MAP_RESET, SystemEvents.PLAYER_PHYSICS_HARD_LAND)
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
        if (globalData.player == null || ComponentsMappers.physics.has(globalData.player)) return
        playerMovementHandler.update(deltaTime, globalData.player!!, dispatcher)
    }


    private fun addPlayer() {
        val playerModelInstance =
            ModelInstance(assetsManger.getAssetByDefinition(ModelsDefinitions.PIG))
        globalData.player = EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(playerModelInstance)
            .addPlayerComponent()
            .finishAndAddToEngine()
        placePlayer()
    }

    private fun addDigit() {
        val digitModelInstance = ModelInstance(digitModel)
        (digitModelInstance.materials.get(0)
            .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
            assetsManger.getAssetByDefinition(TexturesDefinitions.DIGIT_1)
        (digitModelInstance.materials.get(0)).set(
            BlendingAttribute(
                GL20.GL_SRC_ALPHA,
                GL20.GL_ONE_MINUS_SRC_ALPHA
            )
        )
        globalData.digit = EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(
                digitModelInstance,
                Vector3(
                    ComponentsMappers.modelInstance.get(globalData.player).modelInstance.transform.getTranslation(
                        auxVector
                    ).add(0F, 1.4F, 0F)
                )
            )
            .finishAndAddToEngine()
    }

    private fun placePlayer() {
        for (row in SystemsGlobalData.testMapValues.indices) {
            for (col in SystemsGlobalData.testMapValues[0].indices) {
                if (SystemsGlobalData.testMapValues[row][col] == 2) {
                    ComponentsMappers.modelInstance.get(globalData.player).modelInstance.transform.setToTranslation(
                        col + 0.5F, 0F, row + 0.5F
                    ).rotate(Vector3.Y, -90F)
                    return
                }
            }
        }
    }

    companion object {
        val auxVector = Vector3()
    }
}
