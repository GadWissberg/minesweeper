package com.gadarts.minesweeper.systems.data

import com.badlogic.ashley.core.Entity

class PlayerData {
    lateinit var digit: Entity
    var player: Entity? = null
    var coins = 0
}
