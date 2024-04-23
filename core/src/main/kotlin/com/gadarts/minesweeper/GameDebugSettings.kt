package com.gadarts.minesweeper

import com.gadarts.minesweeper.components.player.PowerupType

object GameDebugSettings {

    const val DISPLAY_UI_BORDERS: Boolean = false
    const val SHOW_COLLISION_SHAPES: Boolean = false
    const val CAMERA_CONTROLLER_ENABLED: Boolean = false
    const val SFX_ENABLED: Boolean = true
    const val SHIELD_ON_START: Boolean = false

    @Suppress("RedundantNullableReturnType", "RedundantSuppression")
    val FORCE_CRATES_TO_SPECIFIC_POWER_UP: PowerupType? = null
}
