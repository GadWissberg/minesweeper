package com.gadarts.minesweeper.systems.player.react

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.PlayerData

class PlayerSystemOnShieldConsume : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        assetsManger: GameAssetManager,
        dispatcher: MessageDispatcher,
        engine: Engine,
        soundPlayer: SoundPlayer
    ) {
        if (playerData.invulnerableStepsLeft <= 0) {
            engine.removeEntity(playerData.invulnerableDisplay)
        }
    }

}
