package com.gadarts.minesweeper.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.graphics.Texture

enum class TexturesDefinitions(fileNames: Int = 1) : AssetDefinition<Texture> {

    TILE_UNREVEALED,
    TILE_UNOCCUPIED,
    TILE_BOMBED,
    TILE_DESTINATION,
    TILE_0,
    TILE_1,
    TILE_2,
    TILE_3,
    TILE_4,
    TILE_5,
    TILE_6,
    TILE_7,
    TILE_8,
    DIGIT_1,
    DIGIT_2,
    DIGIT_3,
    DIGIT_4,
    DIGIT_5,
    DIGIT_6,
    DIGIT_7,
    BUTTON_POWERUP_UP,
    BUTTON_POWERUP_DOWN,
    BUTTON_POWERUP_DISABLED,
    ICON_STATUS_COINS,
    ICON_STATUS_SHIELD,
    ICON_BUTTON_SHIELD,
    ICON_BUTTON_EYE;

    private val paths = ArrayList<String>()
    private val pathFormat = "textures/%s.png"

    init {
        initializePaths(pathFormat, fileNames)
    }

    override fun getPaths(): ArrayList<String> {
        return paths
    }

    override fun getParameters(): AssetLoaderParameters<Texture>? {
        return null
    }

    override fun getClazz(): Class<Texture> {
        return Texture::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }

}
