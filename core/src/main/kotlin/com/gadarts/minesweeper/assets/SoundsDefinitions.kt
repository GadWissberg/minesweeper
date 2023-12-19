package com.gadarts.minesweeper.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.audio.Sound

enum class SoundsDefinitions(fileNames: Int = 1, val randomPitch: Boolean = true) :
    AssetDefinition<Sound> {

    JUMP,
    PIG_JUMP(2, true),
    EXPLOSION(3, true),
    WIN(1, true);

    private val paths = ArrayList<String>()
    private val pathFormat = "sounds/%s.wav"

    init {
        initializePaths(pathFormat, fileNames)
    }

    override fun getPaths(): ArrayList<String> {
        return paths
    }

    override fun getParameters(): AssetLoaderParameters<Sound>? {
        return null
    }

    override fun getClazz(): Class<Sound> {
        return Sound::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }

}
