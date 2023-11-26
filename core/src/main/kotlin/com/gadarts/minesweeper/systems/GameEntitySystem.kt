package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.utils.Disposable
import com.gadarts.minesweeper.assets.GameAssetManager


abstract class GameEntitySystem : EntitySystem(), Disposable, Telegraph {
    private lateinit var assetsManger: GameAssetManager
    protected lateinit var globalData: SystemsGlobalData
        private set
    private val dispatcher: MessageDispatcher = MessageDispatcher()

    open fun addListener(listener: GameEntitySystem) {
        listener.getEventsListenList().forEach { event ->
            dispatcher.addListener(
                listener,
                event.ordinal
            )
        }
    }

    private fun getEventsListenList(): List<SystemEvents> = emptyList()
    open fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager
    ) {
        this.globalData = systemsGlobalData
        this.assetsManger = assetsManager
    }

    abstract fun onGlobalDataReady()

}
