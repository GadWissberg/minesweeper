package com.gadarts.minesweeper.systems

import com.gadarts.minesweeper.systems.physics.PhysicsSystem
import com.gadarts.minesweeper.systems.player.PlayerSystemImpl
import com.gadarts.minesweeper.systems.render.RenderSystem

enum class Systems(val systemInstance: GameEntitySystem) {
    CAMERA(CameraSystem()),
    RENDER(RenderSystem()),
    MAP(MapSystem()),
    PHYSICS(PhysicsSystem()),
    PLAYER(PlayerSystemImpl()),
    PARTICLE_EFFECTS(ParticleEffectsSystem()),
    HUD(HudSystem()),
    BONUS(BonusSystem())

}
