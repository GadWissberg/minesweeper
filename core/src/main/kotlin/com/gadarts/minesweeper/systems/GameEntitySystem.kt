package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.utils.Disposable
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.systems.data.SystemsGlobalData


abstract class GameEntitySystem : EntitySystem(), Disposable, Telegraph {
    protected lateinit var soundPlayer: SoundPlayer
    protected lateinit var assetsManger: GameAssetManager
    protected lateinit var globalData: SystemsGlobalData
        private set
    protected lateinit var dispatcher: MessageDispatcher

    abstract fun onSystemReady()
    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        val handlerOnEvent = getSubscribedEvents()[SystemEvents.entries[msg.message]]
        handlerOnEvent?.react(msg, globalData.playerData, assetsManger, dispatcher, engine)
        return handlerOnEvent != null
    }

    open fun addListener(listener: GameEntitySystem) {
        listener.getEventsListenList().forEach { event ->
            dispatcher.addListener(
                listener,
                event.ordinal
            )
        }
        getSubscribedEvents().forEach { dispatcher.addListener(this, it.key.ordinal) }
    }

    protected open fun getSubscribedEvents(): Map<SystemEvents, HandlerOnEvent> {
        return emptyMap()
    }

    open fun initialize(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer,
        dispatcher: MessageDispatcher
    ) {
        this.globalData = systemsGlobalData
        this.assetsManger = assetsManager
        this.soundPlayer = soundPlayer
        this.dispatcher = dispatcher
    }

    protected open fun getEventsListenList(): List<SystemEvents> = emptyList()

}
