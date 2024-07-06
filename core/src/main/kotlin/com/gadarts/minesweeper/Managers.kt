package com.gadarts.minesweeper

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.gadarts.minesweeper.assets.GameAssetManager

class Managers(
    val engine: PooledEngine,
    val soundPlayer: SoundPlayer,
    val assetsManager: GameAssetManager,
    val dispatcher: MessageDispatcher
)
