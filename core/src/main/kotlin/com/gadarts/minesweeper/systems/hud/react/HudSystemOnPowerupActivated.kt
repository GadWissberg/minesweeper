package com.gadarts.minesweeper.systems.hud.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.hud.HudSystem

class HudSystemOnPowerupActivated(private val hudSystem: HudSystem) : HandlerOnEvent {
    override fun react(msg: Telegram, gameSessionData: GameSessionData, managers: Managers) {
        val type = msg.extraInfo as PowerupType
        hudSystem.displayPowerupIndicator(type)
        hudSystem.displayPowerupButton(type)
    }

}
