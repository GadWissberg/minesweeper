package com.gadarts.minesweeper

import com.badlogic.gdx.Game
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.screens.GamePlayScreen

class MineSweeper : Game() {
    override fun create() {
        val assetsManager = GameAssetManager()
        assetsManager.loadAssets()
        setScreen(GamePlayScreen(assetsManager))
    }

    companion object {
        const val PORTRAIT_RESOLUTION_WIDTH = 1080
        const val PORTRAIT_RESOLUTION_HEIGHT = 2400
    }

}
