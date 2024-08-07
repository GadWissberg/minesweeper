package com.gadarts.minesweeper.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.TimeUtils
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.GameDebugSettings
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.assets.ModelsDefinitions
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.GameUtils
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnCurrentTileValueCalculated
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnMapReset
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnMineTriggered
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnPlayerPickedUpBonus
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnPowerupButtonClicked
import com.gadarts.minesweeper.systems.player.react.PlayerSystemOnShieldConsume


class PlayerSystemImpl : GameEntitySystem(), InputProcessor, PlayerSystem {

    private lateinit var digitModel: Model
    private lateinit var regularJumpSound: Sound
    private lateinit var characterJumpSounds: List<Sound>
    private val previousTouchPoint: Vector2 = Vector2()
    private lateinit var playerMovementHandler: PlayerMovementHandler
    override fun initialize(gameSessionData: GameSessionData, managers: Managers) {
        super.initialize(gameSessionData, managers)
        addPlayer(false)
        addDigit()
        playerMovementHandler = PlayerMovementHandler(
            this.gameSessionData.playerData.digit,
            this.gameSessionData.testMapValues
        )
        if (Gdx.input.inputProcessor == null) {
            Gdx.input.inputProcessor = InputMultiplexer(this)
        }
        characterJumpSounds = SoundsDefinitions.PIG_JUMP.getPaths()
            .map { path -> managers.assetsManager.get<Sound>(path) }
            .toList()
        regularJumpSound = managers.assetsManager.getAssetByDefinition(SoundsDefinitions.JUMP)
    }

    override val subscribedEvents: Map<SystemEvents, HandlerOnEvent>
        get() = mapOf(
            SystemEvents.MAP_RESET to PlayerSystemOnMapReset(this),
            SystemEvents.PLAYER_PHYSICS_HARD_LAND to object : HandlerOnEvent {
                override fun react(
                    msg: Telegram,
                    gameSessionData: GameSessionData,
                    managers: Managers
                ) {
                    managers.soundPlayer.playSoundByDefinition(SoundsDefinitions.TAP)
                }
            },
            SystemEvents.TILE_REVEALED to PlayerSystemOnCurrentTileValueCalculated(),
            SystemEvents.POWERUP_BUTTON_CLICKED to PlayerSystemOnPowerupButtonClicked(),
            SystemEvents.MINE_TRIGGERED to PlayerSystemOnMineTriggered(),
            SystemEvents.PLAYER_PICKED_UP_BONUS to PlayerSystemOnPlayerPickedUpBonus(),
            SystemEvents.SHIELD_CONSUME to PlayerSystemOnShieldConsume(),
        )

    override fun onSystemReady() {
        playerBegin()
        if (GameDebugSettings.SHIELD_ON_START) {
            managers.dispatcher.dispatchMessage(
                SystemEvents.POWERUP_BUTTON_CLICKED.ordinal,
                PowerupType.SHIELD
            )
        }
    }

    override fun dispose() {
        digitModel.dispose()
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
            managers.dispatcher,
        )
        if (moved) {
            val sound = if (MathUtils.random(5) == 0) {
                characterJumpSounds.random()
            } else {
                regularJumpSound
            }
            managers.soundPlayer.playSound(sound, 0.5F)
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
        val player = gameSessionData.playerData.player
        if (player == null || ComponentsMappers.physics.has(player)) return
        playerMovementHandler.update(
            deltaTime,
            gameSessionData.playerData,
            managers.dispatcher
        )
        updateInvulnerableEffect(deltaTime, player)
    }

    private fun updateInvulnerableEffect(deltaTime: Float, player: Entity?) {
        if (gameSessionData.playerData.invulnerableStepsLeft > 0) {
            gameSessionData.playerData.invulnerableEffect += deltaTime
            val playerModelInstanceComponent = ComponentsMappers.modelInstance.get(player)
            val sin = MathUtils.sin(gameSessionData.playerData.invulnerableEffect)
            val value = MathUtils.clamp(
                sin,
                0.25F,
                0.75F
            )
            val shieldModelInstanceComponent =
                ComponentsMappers.modelInstance.get(gameSessionData.playerData.invulnerableDisplay)
            (shieldModelInstanceComponent.modelInstance.materials.get(0)
                .get(BlendingAttribute.Type) as BlendingAttribute).opacity =
                value
            shieldModelInstanceComponent.modelInstance.transform.setTranslation(
                playerModelInstanceComponent.modelInstance.transform.getTranslation(
                    auxVector
                )
            )
            val scale =
                Interpolation.exp10.apply(
                    1F,
                    1.2F,
                    MathUtils.sin((gameSessionData.playerData.invulnerableEffect))
                )
            shieldModelInstanceComponent.modelInstance.transform.`val`[Matrix4.M00] = scale
            shieldModelInstanceComponent.modelInstance.transform.`val`[Matrix4.M11] = scale
            shieldModelInstanceComponent.modelInstance.transform.`val`[Matrix4.M22] = scale
            if (gameSessionData.playerData.invulnerableStepsLeft == 1) {
                shieldModelInstanceComponent.visible =
                    ((TimeUtils.millis() / 1000L) % 10L) % 2L == 0L
            }
        }
    }


    override fun addPlayer(resetPlayerMovementHandler: Boolean) {
        val playerModelInstance =
            ModelInstance(managers.assetsManager.getAssetByDefinition(ModelsDefinitions.PIG))
        gameSessionData.playerData.player = EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(
                playerModelInstance,
                managers.assetsManager.getCachedBoundingBox(ModelsDefinitions.PIG)
            )
            .addPlayerComponent()
            .finishAndAddToEngine()
        placePlayer()
        if (resetPlayerMovementHandler) {
            val digitModelInstanceComponent =
                ComponentsMappers.modelInstance.get(gameSessionData.playerData.digit)
            playerMovementHandler.reset(gameSessionData.playerData.player!!)
            digitModelInstanceComponent.visible = false
        }
    }

    override fun playerBegin() {
        managers.dispatcher.dispatchMessage(SystemEvents.PLAYER_BEGIN.ordinal)
        ComponentsMappers.modelInstance.get(gameSessionData.playerData.digit).visible = true
    }

    private fun addDigit() {
        val modelBuilder = ModelBuilder()
        digitModel = GameUtils.createTileModel(modelBuilder, managers.assetsManager, -0.5F)
        val digitModelInstance = ModelInstance(digitModel)
        (digitModelInstance.materials.get(0)
            .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
            managers.assetsManager.getAssetByDefinition(TexturesDefinitions.DIGIT_1)
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
                ),
                digitModel.calculateBoundingBox(auxBoundingBox),
                true
            )
            .finishAndAddToEngine()
    }

    private fun placePlayer() {
        for (row in gameSessionData.testMapValues.indices) {
            for (col in gameSessionData.testMapValues[0].indices) {
                if (gameSessionData.testMapValues[row][col] == 2) {
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
        private val auxBoundingBox = BoundingBox()
    }
}
