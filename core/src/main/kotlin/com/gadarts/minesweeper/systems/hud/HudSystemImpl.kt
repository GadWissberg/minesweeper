package com.gadarts.minesweeper.systems.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.gadarts.minesweeper.GameDebugSettings
import com.gadarts.minesweeper.Services
import com.gadarts.minesweeper.assets.FontsDefinitions
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.hud.react.HudSystemOnPlayerBegin
import com.gadarts.minesweeper.systems.hud.react.HudSystemOnPlayerBlown
import com.gadarts.minesweeper.systems.hud.react.HudSystemOnPlayerPickedUpBonus
import com.gadarts.minesweeper.systems.hud.react.HudSystemOnPowerupActivated
import com.gadarts.minesweeper.systems.hud.react.HudSystemOnShieldConsume


class HudSystemImpl : HudSystem, GameEntitySystem() {

    override var shieldIndicatorTable: Table? = null
    override var shieldStatusLabel: Label? = null
    private lateinit var shieldButton: ImageButton
    private lateinit var xrayButton: ImageButton
    private var shieldIndicatorTableCell: Cell<Table>? = null
    private lateinit var indicatorsTable: Table
    private lateinit var leftSideTable: Table

    override fun initialize(gameSessionData: GameSessionData, services: Services) {
        super.initialize(gameSessionData, services)
        this.gameSessionData.stage =
            Stage(StretchViewport(RESOLUTION_WIDTH.toFloat(), RESOLUTION_HEIGHT.toFloat()))
        (Gdx.input.inputProcessor as InputMultiplexer).addProcessor(0, this.gameSessionData.stage)
        val hudTable = Table()
        hudTable.setSize(this.gameSessionData.stage.width, this.gameSessionData.stage.height)
        this.gameSessionData.stage.addActor(hudTable)
        this.gameSessionData.stage.isDebugAll = GameDebugSettings.DISPLAY_UI_BORDERS
        addComponents(services.assetsManager, hudTable)
    }

    private fun addComponents(
        assetsManager: GameAssetManager,
        hudTable: Table
    ) {
        val coinsIndicator = addCoinsIndicator(assetsManager)
        val rightSideTable = Table()
        shieldButton = addPowerupButton(
            rightSideTable,
            PowerupType.SHIELD,
            TexturesDefinitions.ICON_BUTTON_SHIELD
        )
        xrayButton = addPowerupButton(
            rightSideTable,
            PowerupType.XRAY,
            TexturesDefinitions.ICON_BUTTON_EYE
        )
        leftSideTable = Table()
        hudTable.add(leftSideTable).expand().grow()
        hudTable.add(rightSideTable).expand().top().right()
        indicatorsTable = Table()
        indicatorsTable.add(coinsIndicator).row()
        leftSideTable.add(indicatorsTable).expand().top().left().pad(HUD_COMPONENTS_PADDING).row()
    }

    private fun addPowerupButton(
        rightSideTable: Table,
        type: PowerupType,
        textureDefinition: TexturesDefinitions
    ): ImageButton {
        val style = ImageButtonStyle(
            TextureRegionDrawable(services.assetsManager.getAssetByDefinition(TexturesDefinitions.BUTTON_POWERUP_UP)),
            TextureRegionDrawable(services.assetsManager.getAssetByDefinition(TexturesDefinitions.BUTTON_POWERUP_DOWN)),
            null,
            TextureRegionDrawable(services.assetsManager.getAssetByDefinition(textureDefinition)),
            null,
            null
        )
        style.disabled =
            TextureRegionDrawable(services.assetsManager.getAssetByDefinition(TexturesDefinitions.BUTTON_POWERUP_DISABLED))
        val button = ImageButton(style)
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (button.isDisabled) return
                services.soundPlayer.playSoundByDefinition(SoundsDefinitions.BIP)
                services.dispatcher.dispatchMessage(
                    SystemEvents.POWERUP_BUTTON_CLICKED.ordinal,
                    type
                )
            }
        })
        button.isDisabled = true
        rightSideTable.add(button).expandX().top().right().pad(HUD_COMPONENTS_PADDING).row()
        return button
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

    override val subscribedEvents: Map<SystemEvents, HandlerOnEvent>
        get() = mapOf(
            SystemEvents.PLAYER_PICKED_UP_BONUS to HudSystemOnPlayerPickedUpBonus(this),
            SystemEvents.POWERUP_ACTIVATED to HudSystemOnPowerupActivated(this),
            SystemEvents.SHIELD_CONSUME to HudSystemOnShieldConsume(this),
            SystemEvents.PLAYER_BLOWN to HudSystemOnPlayerBlown(this),
            SystemEvents.PLAYER_BEGIN to HudSystemOnPlayerBegin(this)
        )

    override fun onSystemReady() {

    }


    override fun dispose() {
    }

    override fun displayPowerupIndicator(powerup: PowerupType) {
        if (powerup == PowerupType.SHIELD) {
            shieldStatusLabel = Label(
                gameSessionData.playerData.invulnerableStepsLeft.toString(), Label.LabelStyle(
                    services.assetsManager.getAssetByDefinition(FontsDefinitions.SYMTEXT_100),
                    Color.WHITE
                )
            )
            shieldIndicatorTable = Table()
            shieldIndicatorTable!!.add(
                Image(
                    services.assetsManager.getAssetByDefinition(
                        TexturesDefinitions.ICON_STATUS_SHIELD
                    )
                )
            )
            shieldIndicatorTable!!.add(shieldStatusLabel)
            if (shieldIndicatorTableCell == null) {
                shieldIndicatorTableCell = indicatorsTable.add(shieldIndicatorTable)
            } else {
                shieldIndicatorTableCell!!.setActor(shieldIndicatorTable)
            }
        }
    }

    override fun displayPowerupButton(type: PowerupType) {
        if ((gameSessionData.playerData.powerups[type] ?: 0) <= 0) {
            if (type == PowerupType.SHIELD) {
                shieldButton.isDisabled = true
            } else if (type == PowerupType.XRAY) {
                xrayButton.isDisabled = true
            }
        }
    }

    override fun setPowerupsButtonsTouch(touchable: Touchable) {
        shieldButton.touchable = touchable
        xrayButton.touchable = touchable
    }

    override fun update(deltaTime: Float) {
        gameSessionData.stage.act(deltaTime)
        gameSessionData.stage.draw()
        if (shieldStatusLabel != null && gameSessionData.playerData.invulnerableStepsLeft == 1) {
            shieldStatusLabel!!.isVisible = ((TimeUtils.millis() / 1000L) % 10L) % 2L == 0L
        }
    }

    companion object {
        const val HUD_COMPONENTS_PADDING = 10F
        const val RESOLUTION_WIDTH = 1080
        const val RESOLUTION_HEIGHT = 2400
    }

    override fun setPowerUpButtonState(b: Boolean, powerupType: PowerupType) {
        if (powerupType == PowerupType.SHIELD) {
            shieldButton.isDisabled = b
        } else if (powerupType == PowerupType.XRAY) {
            xrayButton.isDisabled = b
        }
    }

}
