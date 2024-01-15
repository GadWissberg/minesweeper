package com.gadarts.minesweeper.systems

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.gadarts.minesweeper.GameDebugSettings
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.FontsDefinitions
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.systems.data.SystemsGlobalData


class HudSystem : GameEntitySystem() {

    override fun initialize(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer
    ) {
        super.initialize(systemsGlobalData, assetsManager, soundPlayer)
        globalData.stage =
            Stage(StretchViewport(RESOLUTION_WIDTH.toFloat(), RESOLUTION_HEIGHT.toFloat()))
        val hudTable = Table()
        hudTable.setSize(globalData.stage.width, globalData.stage.height)
        globalData.stage.addActor(hudTable)
        globalData.stage.isDebugAll = GameDebugSettings.DISPLAY_UI_BORDERS
        val coinsIndicator = Table()
        coinsIndicator.add(Image(assetsManager.getAssetByDefinition(TexturesDefinitions.COIN)))
        coinsIndicator.add(
            Label(
                "0", Label.LabelStyle(
                    assetsManager.getAssetByDefinition(FontsDefinitions.SYMTEXT_100),
                    Color.WHITE
                )
            )
        )
        hudTable.add(coinsIndicator).expand().top().left().pad(10F)
    }

    override fun onSystemReady() {

    }


    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(SystemEvents.CURRENT_TILE_VALUE_CALCULATED)
    }


    override fun dispose() {
    }

    override fun update(deltaTime: Float) {
        globalData.stage.act(deltaTime)
        globalData.stage.draw()
    }

    companion object {
        const val RESOLUTION_WIDTH = 1080
        const val RESOLUTION_HEIGHT = 2400
    }

}
