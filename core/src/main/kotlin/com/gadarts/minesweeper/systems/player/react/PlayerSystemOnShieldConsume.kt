package com.gadarts.minesweeper.systems.player.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.data.TileData

class PlayerSystemOnShieldConsume : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        mapData: Array<Array<TileData>>
    ) {
        if (playerData.invulnerableStepsLeft <= 0) {
            services.engine.removeEntity(playerData.invulnerableDisplay)
        }
    }
}
