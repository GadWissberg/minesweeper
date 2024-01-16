package com.gadarts.minesweeper.systems.data

import com.badlogic.ashley.core.Entity
import com.gadarts.minesweeper.components.player.PowerupTypes

class PlayerData {
    lateinit var digit: Entity
    val powerups: MutableMap<PowerupTypes, Int> = mutableMapOf()
    var player: Entity? = null
    var coins = 0

    init {
        PowerupTypes.entries.forEach { powerups[it] = 1 }
    }
}
