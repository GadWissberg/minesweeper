package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Matrix4
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

    override fun update(deltaTime: Float) {
        for (crate in crates) {
            updateCrateAnimation(crate)
        }
    }

    private fun updateCrateAnimation(crate: Entity?) {
        val crateComponent = ComponentsMappers.crate.get(crate)
        val modelInstanceComponent = ComponentsMappers.modelInstance.get(crate)
        val interpolation = Interpolation.bounce
        val animationProgress = crateComponent.animationProgress
        modelInstanceComponent.modelInstance.transform.values[Matrix4.M00] =
            interpolation.apply(1F, 1.1F, animationProgress)
        modelInstanceComponent.modelInstance.transform.values[Matrix4.M11] =
            interpolation.apply(1F, 0.8F, animationProgress)
        modelInstanceComponent.modelInstance.transform.values[Matrix4.M22] =
            interpolation.apply(1F, 1.1F, animationProgress)
        crateComponent.animationProgress += 0.01F * if (crateComponent.shrink) -1F else 1F
        if (animationProgress >= 1F) {
            crateComponent.shrink = true
        } else if (animationProgress <= 0F) {
            crateComponent.shrink = false
        }
    }

    companion object {
        val auxVector = Vector3()
        private val auxBoundingBox = BoundingBox()

    }
}
