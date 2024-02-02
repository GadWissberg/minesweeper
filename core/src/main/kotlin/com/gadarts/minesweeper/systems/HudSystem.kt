package com.gadarts.minesweeper.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ai.msg.Telegram
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

    private var shieldIndicatorTable: Table? = null
    private lateinit var indicatorsTable: Table
    private var shieldStatusLabel: Label? = null
    private lateinit var leftSideTable: Table

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
            TextureRegionDrawable(assetsManager.getAssetByDefinition(TexturesDefinitions.ICON_BUTTON_SHIELD)),
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
        leftSideTable = Table()
        val rightSideTable = Table()
        hudTable.add(leftSideTable).expand().grow()
        hudTable.add(rightSideTable).expand().grow()
        rightSideTable.add(shieldButton).expand().top().right().pad(HUD_COMPONENTS_PADDING)
        indicatorsTable = Table()
        indicatorsTable.add(coinsIndicator).row()
        leftSideTable.add(indicatorsTable).expand().top().left().pad(HUD_COMPONENTS_PADDING).row()
    }

    private fun addCoinsIndicator(assetsManager: GameAssetManager): Table {
        val coinsIndicator = Table()
        coinsIndicator.add(Image(assetsManager.getAssetByDefinition(TexturesDefinitions.ICON_STATUS_COINS)))
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
        return listOf(
            SystemEvents.CURRENT_TILE_VALUE_CALCULATED,
            SystemEvents.POWERUP_ACTIVATED,
            SystemEvents.SHIELD_CONSUME
        )
    }


    override fun dispose() {
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        var handled = false
        if (msg.message == SystemEvents.POWERUP_ACTIVATED.ordinal) {
            displayPowerupIndicator(msg)
            handled = true
        } else if (msg.message == SystemEvents.SHIELD_CONSUME.ordinal) {
            val newValue = msg.extraInfo as Int
            if (newValue <= 0) {
                shieldIndicatorTable?.remove()
            } else {
                shieldStatusLabel?.setText(newValue)
            }
        }

        return handled
    }

    private fun displayPowerupIndicator(msg: Telegram) {
        val powerup = msg.extraInfo as PowerupTypes
        if (powerup == PowerupTypes.SHIELD) {
            shieldStatusLabel = Label(
                globalData.playerData.invulnerable.toString(), Label.LabelStyle(
                    assetsManger.getAssetByDefinition(FontsDefinitions.SYMTEXT_100),
                    Color.WHITE
                )
            )
            shieldIndicatorTable = Table()
            shieldIndicatorTable!!.add(Image(assetsManger.getAssetByDefinition(TexturesDefinitions.ICON_STATUS_SHIELD)))
            shieldIndicatorTable!!.add(shieldStatusLabel)
            indicatorsTable.add(shieldIndicatorTable)
        }
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
