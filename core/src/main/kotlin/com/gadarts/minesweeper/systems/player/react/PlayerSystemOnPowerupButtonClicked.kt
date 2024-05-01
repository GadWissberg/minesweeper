package com.gadarts.minesweeper.systems.player.react

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.collision.BoundingBox
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData

class PlayerSystemOnPowerupButtonClicked : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        services: Services
    ) {
        val powerupType = msg.extraInfo as PowerupType
        if (powerupType == PowerupType.SHIELD) {
            if (gameSessionData.playerData.invulnerableStepsLeft <= 0) {
                gameSessionData.playerData.powerups[powerupType] =
                    gameSessionData.playerData.powerups[powerupType]!! - 1
                gameSessionData.playerData.invulnerableStepsLeft = 4
                gameSessionData.playerData.invulnerableEffect = 0F
                services.dispatcher.dispatchMessage(
                    SystemEvents.POWERUP_ACTIVATED.ordinal,
                    powerupType
                )
                services.soundPlayer.playSoundByDefinition(SoundsDefinitions.SHIELD_ACTIVATED)
                gameSessionData.playerData.invulnerableDisplay =
                    EntityBuilder.beginBuildingEntity(services.engine)
                        .addModelInstanceComponent(
                            ModelInstance(
                                gameSessionData.playerData.invulnerableEffectModel,

                                ),
                            gameSessionData.playerData.invulnerableEffectModel.calculateBoundingBox(
                                auxBoundingBox
                            )
                        )
                        .finishAndAddToEngine()
            }
        } else if (powerupType == PowerupType.XRAY) {
            services.dispatcher.dispatchMessage(SystemEvents.POWERUP_ACTIVATED.ordinal, powerupType)
        }
    }

    companion object {
        private val auxBoundingBox = BoundingBox()
    }
}
