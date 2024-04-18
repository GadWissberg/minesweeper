package com.gadarts.minesweeper.systems.player.react

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData

class PlayerSystemOnPowerupButtonClicked : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        tiles: Array<Array<Entity?>>
    ) {
        val powerupType = msg.extraInfo as PowerupType
        if (powerupType == PowerupType.SHIELD) {
            if (playerData.invulnerableStepsLeft <= 0) {
                playerData.powerups[powerupType] = playerData.powerups[powerupType]!! - 1
                playerData.invulnerableStepsLeft = 4
                playerData.invulnerableEffect = 0F
                services.dispatcher.dispatchMessage(
                    SystemEvents.POWERUP_ACTIVATED.ordinal,
                    powerupType
                )
                services.soundPlayer.playSoundByDefinition(SoundsDefinitions.SHIELD_ACTIVATED)
                playerData.invulnerableDisplay = EntityBuilder.beginBuildingEntity(services.engine)
                    .addModelInstanceComponent(ModelInstance(playerData.invulnerableEffectModel))
                    .finishAndAddToEngine()
            }
        } else if (powerupType == PowerupType.XRAY) {
            services.dispatcher.dispatchMessage(SystemEvents.POWERUP_ACTIVATED.ordinal, powerupType)
        }
    }

}
