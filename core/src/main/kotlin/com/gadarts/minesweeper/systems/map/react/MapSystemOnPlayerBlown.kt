package com.gadarts.minesweeper.systems.map.react

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.utils.Timer
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData

class MapSystemOnPlayerBlown :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        gameSessionData: GameSessionData,
        managers: Managers
    ) {
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                managers.dispatcher.dispatchMessage(SystemEvents.MAP_RESET.ordinal)
            }
        }, 5F)
    }

}
