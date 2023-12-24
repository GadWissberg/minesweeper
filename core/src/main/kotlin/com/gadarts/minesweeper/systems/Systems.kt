package com.gadarts.minesweeper.systems

import com.gadarts.minesweeper.systems.physics.PhysicsSystem
import com.gadarts.minesweeper.systems.player.PlayerSystem
import com.gadarts.minesweeper.systems.render.RenderSystem

enum class Systems(val systemInstance: GameEntitySystem) {
    CAMERA(CameraSystem()),
    RENDER(RenderSystem()),
    MAP(MapSystem()),
    PHYSICS(PhysicsSystem()),
    PLAYER(PlayerSystem()),
    PARTICLE_EFFECTS(ParticleEffectsSystem())

}
