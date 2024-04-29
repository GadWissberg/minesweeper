package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.utils.Disposable
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.systems.data.GameSessionData


abstract class GameEntitySystem : EntitySystem(), Disposable, Telegraph {
    protected lateinit var services: Services
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
            services,
        )
        return false
    }

    open fun addListener(listener: GameEntitySystem) {
        subscribedEvents.forEach { services.dispatcher.addListener(this, it.key.ordinal) }
    }


    open fun initialize(
        gameSessionData: GameSessionData,
        services: Services
    ) {
        this.gameSessionData = gameSessionData
        this.services = services
    }

}
