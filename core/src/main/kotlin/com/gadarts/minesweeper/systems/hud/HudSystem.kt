package com.gadarts.minesweeper.systems.hud

import com.gadarts.minesweeper.components.player.PowerupType

interface HudSystem {
    fun setPowerUpButtonState(b: Boolean, powerupType: PowerupType)

}
