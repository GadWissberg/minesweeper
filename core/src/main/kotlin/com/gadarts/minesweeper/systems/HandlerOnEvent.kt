package com.gadarts.minesweeper.systems

import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.systems.data.GameSessionData

interface HandlerOnEvent {
    fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        managers: Managers,
    )

}
