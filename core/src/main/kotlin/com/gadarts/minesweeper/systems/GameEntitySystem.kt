package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.utils.Disposable
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.systems.data.GameSessionData


abstract class GameEntitySystem : EntitySystem(), Disposable, Telegraph {
    protected lateinit var managers: Managers
    protected lateinit var gameSessionData: GameSessionData
        private set
    protected abstract val subscribedEvents: Map<SystemEvents, HandlerOnEvent>

    abstract fun onSystemReady()
    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        val handlerOnEvent = subscribedEvents[SystemEvents.entries[msg.message]]
        handlerOnEvent?.react(
            msg,
            gameSessionData,
            managers,
        )
        return false
    }

    open fun addListener(listener: GameEntitySystem) {
        subscribedEvents.forEach { managers.dispatcher.addListener(this, it.key.ordinal) }
    }


    open fun initialize(
        gameSessionData: GameSessionData,
        managers: Managers
    ) {
        this.gameSessionData = gameSessionData
        this.managers = managers
    }

}
