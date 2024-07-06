package com.gadarts.minesweeper.systems.player.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData

class PlayerSystemOnShieldConsume : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        managers: Managers
    ) {
        if (gameSessionData.playerData.invulnerableStepsLeft <= 0) {
            managers.engine.removeEntity(gameSessionData.playerData.invulnerableDisplay)
        }
    }
}
