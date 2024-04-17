package com.gadarts.minesweeper.systems

import com.gadarts.minesweeper.systems.hud.HudSystemImpl
import com.gadarts.minesweeper.systems.map.MapSystemImpl
import com.gadarts.minesweeper.systems.physics.PhysicsSystem
import com.gadarts.minesweeper.systems.player.PlayerSystemImpl
import com.gadarts.minesweeper.systems.render.RenderSystem

enum class Systems(val systemInstance: GameEntitySystem) {
    CAMERA(CameraSystem()),
    RENDER(RenderSystem()),
    MAP(MapSystemImpl()),
    PHYSICS(PhysicsSystem()),
    PLAYER(PlayerSystemImpl()),
    PARTICLE_EFFECTS(ParticleEffectsSystem()),
    HUD(HudSystemImpl()),
    BONUS(BonusSystem())

}
