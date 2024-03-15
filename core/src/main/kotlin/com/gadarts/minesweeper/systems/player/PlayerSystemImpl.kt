package com.gadarts.minesweeper.systems.player

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.MessageDispatcher
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
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnCurrentTileValueCalculated
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnMapReset
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnMineTriggered
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnPlayerPickedUpBonus
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnPowerupButtonClicked
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnShieldConsume
import kotlin.math.max


class PlayerSystemImpl : GameEntitySystem(), InputProcessor, PlayerSystem {

    private lateinit var digitModel: Model
    private lateinit var regularJumpSound: Sound
    private lateinit var characterJumpSounds: List<Sound>
    private val previousTouchPoint: Vector2 = Vector2()
    private lateinit var playerMovementHandler: PlayerMovementHandler
    override fun initialize(
        gameSessionData: GameSessionData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer,
        dispatcher: MessageDispatcher
    ) {
        super.initialize(gameSessionData, assetsManager, soundPlayer, dispatcher)
        addPlayer(false)
        val modelBuilder = ModelBuilder()
        digitModel = GameUtils.createTileModel(modelBuilder, assetsManager, -0.5F)
        addDigit()
        playerMovementHandler = PlayerMovementHandler(this.gameSessionData.playerData.digit)
        if (Gdx.input.inputProcessor == null) {
            Gdx.input.inputProcessor = InputMultiplexer(this)
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

    override fun getSubscribedEvents(): Map<SystemEvents, HandlerOnEvent> {
        return mapOf(
            SystemEvents.MAP_RESET to PlayerSystemOnMapReset(this),
            SystemEvents.PLAYER_PHYSICS_HARD_LAND to object : HandlerOnEvent {
                override fun react(
                    msg: Telegram,
                    playerData: PlayerData,
                    assetsManger: GameAssetManager,
                    dispatcher: MessageDispatcher,
                    engine: Engine
                ) {
                    soundPlayer.playSoundByDefinition(SoundsDefinitions.TAP)
                }
            },
            SystemEvents.CURRENT_TILE_VALUE_CALCULATED to PlayerSystemOnCurrentTileValueCalculated(),
            SystemEvents.POWERUP_BUTTON_CLICKED to PlayerSystemOnPowerupButtonClicked(),
            SystemEvents.MINE_TRIGGERED to PlayerSystemOnMineTriggered(),
            SystemEvents.PLAYER_PICKED_UP_BONUS to PlayerSystemOnPlayerPickedUpBonus(),
            SystemEvents.SHIELD_CONSUME to PlayerSystemOnShieldConsume(),
        )
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
        if (gameSessionData.playerData.player == null || ComponentsMappers.physics.has(
                gameSessionData.playerData.player
            )
        ) return false

        val moved = playerMovementHandler.movePlayer(
            screenX,
            screenY,
            previousTouchPoint,
            gameSessionData.playerData.player!!,
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
        if (gameSessionData.playerData.player == null || ComponentsMappers.physics.has(
                gameSessionData.playerData.player
            )
        ) return
        playerMovementHandler.update(deltaTime, gameSessionData.playerData, dispatcher)
        if (gameSessionData.playerData.invulnerable > 0) {
            gameSessionData.playerData.invulnerableEffect += 0.1F
            (ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.materials.get(
                0
            ).get(BlendingAttribute.Type) as BlendingAttribute).opacity =
                max(MathUtils.sin(gameSessionData.playerData.invulnerableEffect), 0.5F)
        }
    }


    override fun addPlayer(resetPlayerMovementHandler: Boolean) {
        val playerModelInstance =
            ModelInstance(assetsManger.getAssetByDefinition(ModelsDefinitions.PIG))
        val blendingAttribute = BlendingAttribute()
        blendingAttribute.opacity = 1F
        playerModelInstance.materials.get(0).set(blendingAttribute)
        gameSessionData.playerData.player = EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(playerModelInstance)
            .addPlayerComponent()
            .finishAndAddToEngine()
        placePlayer()
        if (resetPlayerMovementHandler) {
            playerMovementHandler.reset(gameSessionData.playerData.player!!)
            ComponentsMappers.modelInstance.get(gameSessionData.playerData.digit).visible = false
        }
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
        gameSessionData.playerData.digit = EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(
                digitModelInstance,
                Vector3(
                    ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.transform.getTranslation(
                        auxVector
                    ).add(0F, 1.4F, 0F)
                )
            )
            .finishAndAddToEngine()
    }

    private fun placePlayer() {
        for (row in GameSessionData.testMapValues.indices) {
            for (col in GameSessionData.testMapValues[0].indices) {
                if (GameSessionData.testMapValues[row][col] == 2) {
                    ComponentsMappers.modelInstance.get(gameSessionData.playerData.player).modelInstance.transform.setToTranslation(
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
