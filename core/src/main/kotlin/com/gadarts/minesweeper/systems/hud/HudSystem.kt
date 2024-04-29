package com.gadarts.minesweeper.systems.hud

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.gadarts.minesweeper.components.player.PowerupType

interface HudSystem {
    var shieldIndicatorTable: Table?
    var shieldStatusLabel: Label?

    fun setPowerUpButtonState(b: Boolean, powerupType: PowerupType)
    fun displayPowerupIndicator(powerup: PowerupType)
    fun displayPowerupButton(type: PowerupType)
    fun setPowerupsButtonsTouch(touchable: Touchable)

}
