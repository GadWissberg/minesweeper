package com.gadarts.minesweeper.systems.physics

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.badlogic.gdx.utils.Disposable
import com.gadarts.minesweeper.EntityBuilder
import com.gadarts.minesweeper.components.ComponentsMappers
import com.gadarts.minesweeper.systems.CollisionShapesDebugDrawing
import com.gadarts.minesweeper.systems.data.SystemsGlobalData


class BulletEngineHandler(private val globalData: SystemsGlobalData) : Disposable, EntityListener {

    private lateinit var debugDrawer: DebugDrawer
    private lateinit var broadPhase: btAxisSweep3
    private lateinit var ghostPairCallback: btGhostPairCallback
    private lateinit var solver: btSequentialImpulseConstraintSolver
    private lateinit var dispatcher: btCollisionDispatcher
    private lateinit var collisionConfiguration: btDefaultCollisionConfiguration

    private fun initializeDebug() {
        debugDrawer = DebugDrawer()
        debugDrawer.debugMode = btIDebugDraw.DebugDrawModes.DBG_DrawWireframe
        globalData.physicsData.collisionWorld.debugDrawer = debugDrawer
    }

    private fun createGroundPhysicsBody(): btRigidBody {
        val ground = btStaticPlaneShape(auxVector.set(0F, 1F, 0F), 0F)
        val info = btRigidBodyConstructionInfo(
            0f,
            null,
            ground
        )
        val btRigidBody = btRigidBody(info)
        info.dispose()
        btRigidBody.collisionFlags =
            btRigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_STATIC_OBJECT
        btRigidBody.contactCallbackFlag = btBroadphaseProxy.CollisionFilterGroups.KinematicFilter
        return btRigidBody
    }

    private fun initializeBroadPhase() {
        ghostPairCallback = btGhostPairCallback()
        val corner1 = Vector3(-100F, -100F, -100F)
        val corner2 = Vector3(100F, 100F, 100F)
        broadPhase = btAxisSweep3(corner1, corner2)
        broadPhase.overlappingPairCache.setInternalGhostPairCallback(ghostPairCallback)
    }

    override fun entityAdded(entity: Entity?) {
        if (ComponentsMappers.physics.has(entity)) {
            val btRigidBody: btRigidBody = ComponentsMappers.physics.get(entity).rigidBody
            globalData.physicsData.collisionWorld.addRigidBody(btRigidBody)
        }
    }

    override fun entityRemoved(entity: Entity?) {
        if (ComponentsMappers.physics.has(entity)) {
            val physicsComponent = ComponentsMappers.physics[entity]
            physicsComponent.rigidBody.activationState = 0
            globalData.physicsData.collisionWorld.removeCollisionObject(physicsComponent.rigidBody)
            physicsComponent.dispose()
        }
    }

    fun initialize(engine: Engine) {
        Bullet.init()
        collisionConfiguration = btDefaultCollisionConfiguration()
        dispatcher = btCollisionDispatcher(collisionConfiguration)
        solver = btSequentialImpulseConstraintSolver()
        initializeBroadPhase()
        initializeCollisionWorld()
        initializeDebug()
        val btRigidBody = createGroundPhysicsBody()
        globalData.physicsData.collisionWorld.addRigidBody(btRigidBody)
        globalData.physicsData.debugDrawingMethod = object : CollisionShapesDebugDrawing {
            override fun drawCollisionShapes(camera: PerspectiveCamera) {
                debugDrawer.begin(camera)
                globalData.physicsData.collisionWorld.debugDrawWorld()
                debugDrawer.end()
            }
        }
        btRigidBody.userData =
            EntityBuilder.beginBuildingEntity(engine).addGroundComponent().finishAndAddToEngine()
        engine.addEntityListener(this)
    }

    private fun initializeCollisionWorld() {
        globalData.physicsData.collisionWorld = btDiscreteDynamicsWorld(
            dispatcher,
            broadPhase,
            solver,
            collisionConfiguration
        )
        globalData.physicsData.collisionWorld.gravity = Vector3(0F, GRAVITY_FORCE, 0F)
    }

    override fun dispose() {
        collisionConfiguration.dispose()
        solver.dispose()
        dispatcher.dispose()
        ghostPairCallback.dispose()
        broadPhase.dispose()
        debugDrawer.dispose()
    }

    fun update(deltaTime: Float) {
        globalData.physicsData.collisionWorld.stepSimulation(
            deltaTime,
            5,
            1f / 60F
        )
    }


    companion object {
        const val GRAVITY_FORCE = -9.8f
        val auxVector = Vector3()
    }

}
