package com.gadarts.minesweeper.systems.data

import com.badlogic.ashley.core.Entity
import com.gadarts.minesweeper.components.player.PowerupTypes

class PlayerData {

    var invulnerableEffect: Float = 0F
    var invulnerable: Int = 0

    lateinit var digit: Entity
    val powerups: MutableMap<PowerupTypes, Int> = mutableMapOf()
    var player: Entity? = null
    var coins = 0

    fun reset() {
        invulnerable = 0
    }
}
