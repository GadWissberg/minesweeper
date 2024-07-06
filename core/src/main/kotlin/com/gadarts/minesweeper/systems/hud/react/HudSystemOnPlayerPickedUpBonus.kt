package com.gadarts.minesweeper.systems.hud.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.hud.HudSystem

class HudSystemOnPlayerPickedUpBonus(private val hudSystem: HudSystem) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        managers: Managers
    ) {
        hudSystem.setPowerUpButtonState(false, msg.extraInfo as PowerupType)
    }
}
