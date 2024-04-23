package com.gadarts.minesweeper.systems.map.react

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.utils.Timer
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData

class MapSystemOnPlayerBlown :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        tiles: Array<Array<Entity?>>,
        testMapValues: Array<Array<Int>>
    ) {
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                services.dispatcher.dispatchMessage(SystemEvents.MAP_RESET.ordinal)
            }
        }, 5F)
    }

}
