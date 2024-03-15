package com.gadarts.minesweeper.systems.hud

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
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
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.gadarts.minesweeper.GameDebugSettings
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.FontsDefinitions
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.TexturesDefinitions
import com.gadarts.minesweeper.components.player.PowerupTypes
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.PlayerData
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.hud.react.HudSystemOnPlayerPickedUpBonus


class HudSystemImpl : HudSystem, GameEntitySystem() {

    private lateinit var shieldButton: ImageButton
    private var shieldIndicatorTableCell: Cell<Table>? = null
    private var shieldIndicatorTable: Table? = null
    private lateinit var indicatorsTable: Table
    private var shieldStatusLabel: Label? = null
    private lateinit var leftSideTable: Table

    override fun initialize(
        gameSessionData: GameSessionData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer,
        dispatcher: MessageDispatcher
    ) {
        super.initialize(gameSessionData, assetsManager, soundPlayer, dispatcher)
        this.gameSessionData.stage =
            Stage(StretchViewport(RESOLUTION_WIDTH.toFloat(), RESOLUTION_HEIGHT.toFloat()))
        (Gdx.input.inputProcessor as InputMultiplexer).addProcessor(0, this.gameSessionData.stage)
        val hudTable = Table()
        hudTable.setSize(this.gameSessionData.stage.width, this.gameSessionData.stage.height)
        this.gameSessionData.stage.addActor(hudTable)
        this.gameSessionData.stage.isDebugAll = GameDebugSettings.DISPLAY_UI_BORDERS
        addComponents(assetsManager, hudTable)
    }

    override fun getSubscribedEvents(): Map<SystemEvents, HandlerOnEvent> {
        return mapOf(
            SystemEvents.PLAYER_PICKED_UP_BONUS to HudSystemOnPlayerPickedUpBonus(this),
            SystemEvents.POWERUP_ACTIVATED to object : HandlerOnEvent {
                override fun react(
                    msg: Telegram,
                    playerData: PlayerData,
                    assetsManger: GameAssetManager,
                    dispatcher: MessageDispatcher,
                    engine: Engine
                ) {
                    displayPowerupIndicator(msg)
                    if ((playerData.powerups[PowerupTypes.SHIELD] ?: 0) <= 0) {
                        shieldButton.isDisabled = true
                    }
                }
            },
            SystemEvents.SHIELD_CONSUME to object : HandlerOnEvent {
                override fun react(
                    msg: Telegram,
                    playerData: PlayerData,
                    assetsManger: GameAssetManager,
                    dispatcher: MessageDispatcher,
                    engine: Engine
                ) {
                    val newValue = msg.extraInfo as Int
                    if (newValue <= 0) {
                        shieldIndicatorTable?.remove()
                    } else {
                        shieldStatusLabel?.setText(newValue)
                    }
                }
            },
            SystemEvents.PLAYER_BLOWN to object : HandlerOnEvent {
                override fun react(
                    msg: Telegram,
                    playerData: PlayerData,
                    assetsManger: GameAssetManager,
                    dispatcher: MessageDispatcher,
                    engine: Engine
                ) {
                    shieldButton.touchable = Touchable.disabled
                }
            },
            SystemEvents.PLAYER_BEGIN to object : HandlerOnEvent {
                override fun react(
                    msg: Telegram,
                    playerData: PlayerData,
                    assetsManger: GameAssetManager,
                    dispatcher: MessageDispatcher,
                    engine: Engine
                ) {
                    shieldButton.touchable = Touchable.enabled
                }
            }
        )
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
        style.disabled =
            TextureRegionDrawable(assetsManager.getAssetByDefinition(TexturesDefinitions.BUTTON_POWERUP_DISABLED))
        shieldButton = ImageButton(style)
        shieldButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (shieldButton.isDisabled) return
                dispatcher.dispatchMessage(
                    SystemEvents.POWERUP_BUTTON_CLICKED.ordinal,
                    PowerupTypes.SHIELD
                )
            }
        })
        shieldButton.isDisabled = true
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


    override fun dispose() {
    }

    fun displayPowerupIndicator(msg: Telegram) {
        val powerup = msg.extraInfo as PowerupTypes
        if (powerup == PowerupTypes.SHIELD) {
            shieldStatusLabel = Label(
                gameSessionData.playerData.invulnerable.toString(), Label.LabelStyle(
                    assetsManger.getAssetByDefinition(FontsDefinitions.SYMTEXT_100),
                    Color.WHITE
                )
            )
            shieldIndicatorTable = Table()
            shieldIndicatorTable!!.add(Image(assetsManger.getAssetByDefinition(TexturesDefinitions.ICON_STATUS_SHIELD)))
            shieldIndicatorTable!!.add(shieldStatusLabel)
            if (shieldIndicatorTableCell == null) {
                shieldIndicatorTableCell = indicatorsTable.add(shieldIndicatorTable)
            } else {
                shieldIndicatorTableCell!!.setActor(shieldIndicatorTable)
            }
        }
    }

    override fun update(deltaTime: Float) {
        gameSessionData.stage.act(deltaTime)
        gameSessionData.stage.draw()
    }

    companion object {
        const val HUD_COMPONENTS_PADDING = 10F
        const val RESOLUTION_WIDTH = 1080
        const val RESOLUTION_HEIGHT = 2400
    }

    override fun setShieldButtonState(b: Boolean) {
        shieldButton.isDisabled = b
    }

}
