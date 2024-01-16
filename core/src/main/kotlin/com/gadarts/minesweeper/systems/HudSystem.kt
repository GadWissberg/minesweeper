package com.gadarts.minesweeper.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.gadarts.minesweeper.GameDebugSettings
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.FontsDefinitions
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.player.PowerupTypes
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
        (Gdx.input.inputProcessor as InputMultiplexer).addProcessor(0, globalData.stage)
        val hudTable = Table()
        hudTable.setSize(globalData.stage.width, globalData.stage.height)
        globalData.stage.addActor(hudTable)
        globalData.stage.isDebugAll = GameDebugSettings.DISPLAY_UI_BORDERS
        addComponents(assetsManager, hudTable)
    }

    private fun addComponents(
        assetsManager: GameAssetManager,
        hudTable: Table
    ) {
        val coinsIndicator = addCoinsIndicator(assetsManager)
        val style = ImageButtonStyle(
            TextureRegionDrawable(assetsManager.getAssetByDefinition(TexturesDefinitions.BUTTON_POWERUP_UP)),
            TextureRegionDrawable(assetsManager.getAssetByDefinition(TexturesDefinitions.BUTTON_POWERUP_DOWN)),
            null,
            TextureRegionDrawable(assetsManager.getAssetByDefinition(TexturesDefinitions.POWERUP_ICON_SHIELD)),
            null,
            null
        )
        val shieldButton = ImageButton(style)
        shieldButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                dispatcher.dispatchMessage(
                    SystemEvents.POWERUP_BUTTON_CLICKED.ordinal,
                    PowerupTypes.SHIELD
                )
            }
        })
        val leftSideTable = Table()
        val rightSideTable = Table()
        hudTable.add(leftSideTable).expand().grow()
        hudTable.add(rightSideTable).expand().grow()
        rightSideTable.add(shieldButton).expand().top().right().pad(HUD_COMPONENTS_PADDING)
        leftSideTable.add(coinsIndicator).expand().top().left().pad(HUD_COMPONENTS_PADDING)
    }

    private fun addCoinsIndicator(assetsManager: GameAssetManager): Table {
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
        return coinsIndicator
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
        const val HUD_COMPONENTS_PADDING = 10F
        const val RESOLUTION_WIDTH = 1080
        const val RESOLUTION_HEIGHT = 2400
    }

}
