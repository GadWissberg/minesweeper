package com.gadarts.minesweeper.systems.player.react

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData

class PlayerSystemOnPowerupButtonClicked : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        assetsManger: GameAssetManager,
        dispatcher: MessageDispatcher,
        engine: Engine,
        soundPlayer: SoundPlayer
    ) {
        if (playerData.invulnerableStepsLeft <= 0) {
            val type = msg.extraInfo as PowerupType
            playerData.powerups[type] = playerData.powerups[type]!! - 1
            playerData.invulnerableStepsLeft = 4
            playerData.invulnerableEffect = 0F
            dispatcher.dispatchMessage(SystemEvents.POWERUP_ACTIVATED.ordinal, type)
            soundPlayer.playSoundByDefinition(SoundsDefinitions.SHIELD_ACTIVATED)
            playerData.invulnerableDisplay = EntityBuilder.beginBuildingEntity(engine)
                .addModelInstanceComponent(ModelInstance(playerData.invulnerableEffectModel))
                .finishAndAddToEngine()
        }
    }

}
