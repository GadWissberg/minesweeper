package com.gadarts.minesweeper.systems.player.react

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.data.PlayerData

class PlayerSystemOnPlayerPickedUpBonus : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        playerData: PlayerData,
        services: Services,
        tiles: Array<Array<Entity?>>
    ) {
        val bonus = msg.extraInfo as PowerupType
        playerData.powerups[bonus] = (playerData.powerups[bonus] ?: 0).plus(1)
    }
}
