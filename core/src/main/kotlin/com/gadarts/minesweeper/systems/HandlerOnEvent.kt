package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.systems.data.PlayerData

interface HandlerOnEvent {
    fun react(
        msg: Telegram,
        playerData: PlayerData,
        assetsManger: GameAssetManager,
        dispatcher: MessageDispatcher,
        engine: Engine
    )

}
