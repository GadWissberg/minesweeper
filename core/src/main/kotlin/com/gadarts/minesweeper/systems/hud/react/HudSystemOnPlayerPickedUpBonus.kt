package com.gadarts.minesweeper.systems.hud.react

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.hud.HudSystem

class HudSystemOnPlayerPickedUpBonus(private val hudSystem: HudSystem) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        assetsManger: GameAssetManager,
        dispatcher: MessageDispatcher,
        engine: Engine
    ) {
        hudSystem.setShieldButtonState(false)
    }

}
