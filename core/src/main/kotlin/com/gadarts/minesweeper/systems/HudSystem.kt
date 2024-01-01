package com.gadarts.minesweeper.systems

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.FontsDefinitions
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.systems.data.SystemsGlobalData


class HudSystem : GameEntitySystem() {
    private lateinit var bombAroundLabel: Label

    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager, soundPlayer)
        globalData.stage =
            Stage(StretchViewport(RESOLUTION_WIDTH.toFloat(), RESOLUTION_HEIGHT.toFloat()))
        val hudTable = Table()
        hudTable.setSize(globalData.stage.width, globalData.stage.height)
        globalData.stage.addActor(hudTable)
        bombAroundLabel = Label(
            "1", Label.LabelStyle(
                assetsManager.getAssetByDefinition(FontsDefinitions.SYMTEXT),
                Color.WHITE
            )
        )
        bombAroundLabel.setAlignment(Align.center)
        hudTable.add(bombAroundLabel).expand().pad(20F).top()
    }

    override fun onGlobalDataReady() {

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

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.CURRENT_TILE_VALUE_CALCULATED.ordinal) {
            bombAroundLabel.setText(msg.extraInfo as Int)
        }

        return false
    }

    companion object {
        const val RESOLUTION_WIDTH = 1080
        const val RESOLUTION_HEIGHT = 2400
    }

}
