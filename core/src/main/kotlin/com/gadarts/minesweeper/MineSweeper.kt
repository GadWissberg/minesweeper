package com.gadarts.minesweeper

import com.badlogic.gdx.Game
import com.gadarts.minesweeper.screens.GamePlayScreen

class MineSweeper : Game() {
    override fun create() {
        setScreen(GamePlayScreen());
    }

    companion object {
        const val PORTRAIT_RESOLUTION_WIDTH = 1080
        const val PORTRAIT_RESOLUTION_HEIGHT = 2400
    }

}
