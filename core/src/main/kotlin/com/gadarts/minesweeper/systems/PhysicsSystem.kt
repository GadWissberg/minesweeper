package com.gadarts.minesweeper.systems

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.SoundPlayer
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.physics.BulletEngineHandler

class PhysicsSystem : GameEntitySystem() {
    private lateinit var bulletEngineHandler: BulletEngineHandler

    override fun createGlobalData(
        systemsGlobalData: SystemsGlobalData,
        assetsManager: GameAssetManager,
        soundPlayer: SoundPlayer
    ) {
        super.createGlobalData(systemsGlobalData, assetsManager, soundPlayer)
        bulletEngineHandler = BulletEngineHandler(globalData)
        bulletEngineHandler.initialize(engine)
    }

    override fun getEventsListenList(): List<SystemEvents> {
        return listOf(SystemEvents.MINE_TRIGGERED)
    }

    override fun onGlobalDataReady() {

    }

    override fun dispose() {
        bulletEngineHandler.dispose()
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        bulletEngineHandler.update(deltaTime)
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg == null) return false

        if (msg.message == SystemEvents.MINE_TRIGGERED.ordinal) {
            val physicsComponent = EntityBuilder.createPhysicsComponent(
                ComponentsMappers.modelInstance.get(globalData.player).modelInstance.calculateBoundingBox(
                    auxBoundingBox
                ),
                ComponentsMappers.modelInstance.get(globalData.player).modelInstance.transform,
                engine as PooledEngine
            )
            globalData.player?.add(physicsComponent)
            globalData.collisionWorld.addRigidBody(physicsComponent.rigidBody)
            physicsComponent.rigidBody.applyImpulse(
                Vector3(
                    MathUtils.random(-1F, 1F),
                    MathUtils.random(0F, 1F),
                    MathUtils.random(-1F, 1F)
                ).scl(95F),
                Vector3(
                    MathUtils.random(-0.1F, 0.1F),
                    0F,
                    MathUtils.random(-0.1F, 0.1F)
                )
            )
        }

        return false
    }

    companion object {
        val auxBoundingBox = BoundingBox()
    }
}
