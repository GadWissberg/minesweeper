package com.gadarts.minesweeper.systems

import com.gadarts.minesweeper.systems.player.PlayerSystem
import com.gadarts.minesweeper.systems.render.RenderSystem

enum class Systems(val systemInstance: GameEntitySystem) {
    CAMERA(CameraSystem()),
    RENDER(RenderSystem()),
    MAP(MapSystem()),
    PLAYER(PlayerSystem())

}
