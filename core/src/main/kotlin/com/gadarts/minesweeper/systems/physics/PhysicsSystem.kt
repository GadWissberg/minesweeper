package com.gadarts.minesweeper.systems.physics

import com.badlogic.gdx.math.collision.BoundingBox
import com.gadarts.minesweeper.Managers
import com.gadarts.minesweeper.systems.GameEntitySystem
import com.gadarts.minesweeper.systems.HandlerOnEvent
import com.gadarts.minesweeper.systems.SystemEvents
import com.gadarts.minesweeper.systems.data.GameSessionData
import com.gadarts.minesweeper.systems.physics.react.PhysicsSystemOnPlayerBlown
import com.gadarts.minesweeper.systems.physics.react.PhysicsSystemOnPlayerIsAboutToBeRemoved

class PhysicsSystem : GameEntitySystem() {
    private lateinit var contactListener: GameContactListener
    private lateinit var bulletEngineHandler: BulletEngineHandler

    override fun initialize(gameSessionData: GameSessionData, managers: Managers) {
        super.initialize(gameSessionData, managers)
        bulletEngineHandler = BulletEngineHandler(this.gameSessionData)
        bulletEngineHandler.initialize(engine)
        contactListener = GameContactListener(managers.dispatcher)
    }

    override fun dispose() {
        bulletEngineHandler.dispose()
        contactListener.dispose()
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        bulletEngineHandler.update(deltaTime)
    }

    override val subscribedEvents: Map<SystemEvents, HandlerOnEvent>
        get() = mapOf(
            SystemEvents.PLAYER_BLOWN to PhysicsSystemOnPlayerBlown(),
            SystemEvents.PLAYER_IS_ABOUT_TO_BE_REMOVED to PhysicsSystemOnPlayerIsAboutToBeRemoved()
        )

    override fun onSystemReady() {
    }

    companion object {
        val auxBoundingBox = BoundingBox()
    }
}
