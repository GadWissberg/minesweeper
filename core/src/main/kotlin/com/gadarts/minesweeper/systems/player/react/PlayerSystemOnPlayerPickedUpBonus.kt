package com.gadarts.minesweeper.systems.player.react

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.GameSessionData

class PlayerSystemOnPlayerPickedUpBonus : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        managers: Managers
    ) {
        val bonus = msg.extraInfo as PowerupType
        gameSessionData.playerData.powerups[bonus] =
            (gameSessionData.playerData.powerups[bonus] ?: 0).plus(1)
    }
}
