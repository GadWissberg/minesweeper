package com.gadarts.minesweeper.systems.hud.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.hud.HudSystem

class HudSystemOnShieldConsume(private val hudSystem: HudSystem) : HandlerOnEvent {
    override fun react(msg: Telegram, gameSessionData: GameSessionData, services: Services) {
        val newValue = msg.extraInfo as Int
        if (newValue <= 0) {
            hudSystem.shieldIndicatorTable?.remove()
            services.soundPlayer.playSoundByDefinition(SoundsDefinitions.SHIELD_DEPLETED)
        } else {
            hudSystem.shieldStatusLabel?.setText(newValue)
        }
    }

}
