package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.GameDebugSettings
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.ModelsDefinitions
import com.gadarts.minesweeper.assets.ParticleEffectsDefinitions
import com.gadarts.minesweeper.assets.SoundsDefinitions
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.components.CrateComponent
import com.gadarts.minesweeper.components.player.PowerupType
import com.gadarts.minesweeper.systems.data.GameSessionData

class BonusSystem : GameEntitySystem() {
    private lateinit var crates: ImmutableArray<Entity>

    override fun initialize(gameSessionData: GameSessionData, managers: Managers) {
        super.initialize(gameSessionData, managers)
        crates = engine.getEntitiesFor(Family.all(CrateComponent::class.java).get())
        addCrates(managers.assetsManager)
    }

    private fun addCrates(assetsManager: GameAssetManager) {
        for (row in gameSessionData.testMapValues.indices) {
            for (col in gameSessionData.testMapValues[0].indices) {
                if (gameSessionData.testMapValues[row][col] == 5) {
                    val modelInstance = ModelInstance(
                        assetsManager.getAssetByDefinition(ModelsDefinitions.CRATE)
                    )
                    auxBoundingBox.set(assetsManager.getCachedBoundingBox(ModelsDefinitions.CRATE))
                    val entity = EntityBuilder.beginBuildingEntity(engine)
                        .addModelInstanceComponent(
                            modelInstance,
                            Vector3(col + 0.5F, auxBoundingBox.height / 2, row + 0.5F),
                            auxBoundingBox
                        )
                        .addCrateComponent()
                        .addShrinkAnimationComponent(
                            1.1F,
                            0.8F,
                            1.1F,
                            modelInstance.transform,
                            Interpolation.bounce,
                            0.01F
                        )
                        .finishAndAddToEngine()
                    ComponentsMappers.tile.get(gameSessionData.tiles[row][col]).crate = entity
                }
            }
        }
    }

    override val subscribedEvents: Map<SystemEvents, HandlerOnEvent>
        get() = mapOf(SystemEvents.PLAYER_LANDED to object : HandlerOnEvent {
            override fun react(
                msg: Telegram,
                gameSessionData: GameSessionData,
                managers: Managers
            ) {
                val position =
                    ComponentsMappers.modelInstance.get(this@BonusSystem.gameSessionData.playerData.player).modelInstance.transform.getTranslation(
                        auxVector
                    )
                val row = position.z.toInt()
                val col = position.x.toInt()
                val tileComponent =
                    ComponentsMappers.tile.get(this@BonusSystem.gameSessionData.tiles[row][col])
                if (this@BonusSystem.gameSessionData.testMapValues[row][col] == 5 && tileComponent.crate != null
                ) {
                    this@BonusSystem.gameSessionData.testMapValues[row][col] = 0
                    engine.removeEntity(tileComponent.crate)
                    EntityBuilder.beginBuildingEntity(engine).addParticleEffectComponent(
                        managers.assetsManager.getAssetByDefinition(ParticleEffectsDefinitions.CRATE_PARTICLES),
                        position
                    ).finishAndAddToEngine()
                    managers.dispatcher.dispatchMessage(
                        SystemEvents.PLAYER_PICKED_UP_BONUS.ordinal,
                        GameDebugSettings.FORCE_CRATES_TO_SPECIFIC_POWER_UP
                            ?: PowerupType.entries.random()
                    )
                    managers.soundPlayer.playSoundByDefinition(SoundsDefinitions.BONUS)
                }
            }
        })

    override fun onSystemReady() {
    }

    override fun dispose() {
    }


    companion object {
        val auxVector = Vector3()
        private val auxBoundingBox = BoundingBox()

    }
}
