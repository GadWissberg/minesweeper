package com.gadarts.minesweeper.systems.player.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData

class PlayerSystemOnShieldConsume : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        services: Services
    ) {
        if (gameSessionData.playerData.invulnerableStepsLeft <= 0) {
            services.engine.removeEntity(gameSessionData.playerData.invulnerableDisplay)
        }
    }
}
