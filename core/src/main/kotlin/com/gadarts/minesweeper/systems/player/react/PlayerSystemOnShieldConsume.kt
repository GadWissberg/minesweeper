package com.gadarts.minesweeper.systems.player.react

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.PlayerData

class PlayerSystemOnShieldConsume : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        tiles: Array<Array<Entity?>>,
        testMapValues: Array<Array<Int>>
    ) {
        if (playerData.invulnerableStepsLeft <= 0) {
            services.engine.removeEntity(playerData.invulnerableDisplay)
        }
    }
}
