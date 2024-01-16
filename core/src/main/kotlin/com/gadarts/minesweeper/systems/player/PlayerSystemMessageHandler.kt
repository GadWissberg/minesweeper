package com.gadarts.minesweeper.systems.player

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.player.PowerupTypes
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData

class PlayerSystemMessageHandler(
    private val soundPlayer: SoundPlayer,
    private val assetsManager: GameAssetManager
) {
    private val mapping =
        mapOf<Int, (Telegram, PlayerData, Engine, MessageDispatcher, PlayerSystem) -> Unit>(
            SystemEvents.MAP_RESET.ordinal to { _, playerData, engine, dispatcher, playerSystem ->
                ComponentsMappers.modelInstance.get(playerData.digit).visible = false
                engine.removeEntity(playerData.player)
                playerData.player = null
                Gdx.app.postRunnable {
                    playerSystem.addPlayer(true)
                    dispatcher.dispatchMessage(SystemEvents.PLAYER_BEGIN.ordinal)
                }
            },
            SystemEvents.PLAYER_PHYSICS_HARD_LAND.ordinal to { _, _, _, _, _ ->
                soundPlayer.playSoundByDefinition(SoundsDefinitions.TAP)
            },
            SystemEvents.CURRENT_TILE_VALUE_CALCULATED.ordinal to { msg, playerData, _, _, _ ->
                val definition = sumToTextureDefinition[msg.extraInfo as Int]
                if (definition != null) {
                    ComponentsMappers.modelInstance.get(playerData.digit).visible = true
                    (ComponentsMappers.modelInstance.get(playerData.digit).modelInstance.materials.get(
                        0
                    )
                        .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
                        assetsManager.getAssetByDefinition(definition)
                } else {
                    ComponentsMappers.modelInstance.get(playerData.digit).visible = false
                }
            },
            SystemEvents.POWERUP_BUTTON_CLICKED.ordinal to { msg, playerData, _, _, _ ->
                val type = msg.extraInfo as PowerupTypes
                playerData.powerups[type] = playerData.powerups[type]!! - 1
            }
        )

    fun handle(
        msg: Telegram,
        playerData: PlayerData,
        engine: Engine,
        dispatcher: MessageDispatcher,
        playerSystem: PlayerSystem
    ): Boolean {
        if (mapping.containsKey(msg.message)) {
            mapping[msg.message]!!.invoke(msg, playerData, engine, dispatcher, playerSystem)
            return true
        }
        return false
    }

    companion object {
        private val sumToTextureDefinition = listOf(
            null,
            TexturesDefinitions.DIGIT_1,
            TexturesDefinitions.DIGIT_2,
            TexturesDefinitions.DIGIT_3,
            TexturesDefinitions.DIGIT_4,
            TexturesDefinitions.DIGIT_5,
            TexturesDefinitions.DIGIT_6,
            TexturesDefinitions.DIGIT_7,
        )

    }
}
