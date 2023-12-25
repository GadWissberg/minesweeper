package com.gadarts.minesweeper.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader

enum class FontsDefinitions : AssetDefinition<BitmapFont> {

    SYMTEXT {
        override fun getParameters(): AssetLoaderParameters<BitmapFont> {
            val params = FreetypeFontLoader.FreeTypeFontLoaderParameter()
            params.fontFileName = "$fontFolderName/symtext.$fontFileType"
            params.fontParameters.size = 225
            params.fontParameters.color = Color.WHITE
            params.fontParameters.borderColor = Color(0f, 0F, 0f, 0.5f)
            params.fontParameters.borderWidth = 8F
            params.fontParameters.shadowColor = Color(0f, 0F, 0f, 0.5f)
            params.fontParameters.shadowOffsetX = -4
            params.fontParameters.shadowOffsetY = 4
            params.fontParameters.borderStraight = true
            params.fontParameters.kerning = true
            return params
        }
    };

    val fontFileType: String = "ttf"
    val fontFolderName: String = "fonts"
    private val paths = ArrayList<String>()
    private val pathFormat = "$fontFolderName/%s.$fontFileType"

    init {
        initializePaths(pathFormat)
    }

    override fun getPaths(): ArrayList<String> {
        return paths
    }

    override fun getClazz(): Class<BitmapFont> {
        return BitmapFont::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }

}
