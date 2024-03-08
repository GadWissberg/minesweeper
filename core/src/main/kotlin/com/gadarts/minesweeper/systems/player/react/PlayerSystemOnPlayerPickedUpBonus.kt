package com.gadarts.minesweeper.systems.player.react

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.components.player.PowerupTypes
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.PlayerData

class PlayerSystemOnPlayerPickedUpBonus : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        assetsManger: GameAssetManager,
        dispatcher: MessageDispatcher,
        engine: Engine
    ) {
        playerData.powerups[PowerupTypes.SHIELD] =
            (playerData.powerups[PowerupTypes.SHIELD] ?: 0).plus(1)
    }

}
