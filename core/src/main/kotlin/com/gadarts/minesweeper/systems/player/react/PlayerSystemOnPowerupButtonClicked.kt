package com.gadarts.minesweeper.systems.player.react

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.components.player.PowerupTypes
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData

class PlayerSystemOnPowerupButtonClicked : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        assetsManger: GameAssetManager,
        dispatcher: MessageDispatcher,
        engine: Engine
    ) {
        if (playerData.invulnerable <= 0) {
            val type = msg.extraInfo as PowerupTypes
            playerData.powerups[type] = playerData.powerups[type]!! - 1
            playerData.invulnerable = 4
            dispatcher.dispatchMessage(SystemEvents.POWERUP_ACTIVATED.ordinal, type)
        }
    }

}
