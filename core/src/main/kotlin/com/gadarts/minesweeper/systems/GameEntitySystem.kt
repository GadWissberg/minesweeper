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

    abstract fun onSystemReady()
    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        val handlerOnEvent = getSubscribedEvents()[SystemEvents.entries[msg.message]]
        handlerOnEvent?.react(
            msg,
            gameSessionData.playerData,
            services,
            gameSessionData.mapData
        )
        return handlerOnEvent != null
    }

    open fun addListener(listener: GameEntitySystem) {
        listener.getEventsListenList().forEach { event ->
            services.dispatcher.addListener(
                listener,
                event.ordinal
            )
        }
        getSubscribedEvents().forEach { services.dispatcher.addListener(this, it.key.ordinal) }
    }

    protected open fun getSubscribedEvents(): Map<SystemEvents, HandlerOnEvent> {
        return emptyMap()
    }

    open fun initialize(
        gameSessionData: GameSessionData,
        services: Services
    ) {
        this.gameSessionData = gameSessionData
        this.services = services
    }

    protected open fun getEventsListenList(): List<SystemEvents> = emptyList()

}
