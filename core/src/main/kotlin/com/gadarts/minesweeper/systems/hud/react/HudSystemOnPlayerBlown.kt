package com.gadarts.minesweeper.systems.hud.react

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.hud.HudSystem

class HudSystemOnPlayerBlown(private val hudSystem: HudSystem) : HandlerOnEvent {
    override fun react(msg: Telegram, gameSessionData: GameSessionData, managers: Managers) {
        hudSystem.setPowerupsButtonsTouch(Touchable.disabled)
    }

}
