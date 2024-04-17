package com.gadarts.minesweeper.systems.hud.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.data.TileData
import com.gadarts.minesweeper.systems.hud.HudSystem

class HudSystemOnPlayerPickedUpBonus(private val hudSystem: HudSystem) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        mapData: Array<Array<TileData>>
    ) {
        hudSystem.setPowerUpButtonState(false, msg.extraInfo as PowerupType)
    }
}
