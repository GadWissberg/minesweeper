package com.gadarts.minesweeper.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader

enum class FontsDefinitions : AssetDefinition<BitmapFont> {

    SYMTEXT_225 {
        override fun getParameters(): AssetLoaderParameters<BitmapFont> {
            return createParams(225)
        }

    },
    SYMTEXT_100 {
        override fun getParameters(): AssetLoaderParameters<BitmapFont> {
            return createParams(100)
        }
    },
    SYMTEXT_25 {
        override fun getParameters(): AssetLoaderParameters<BitmapFont> {
            return createParams(25)
        }
    };


    private val paths = ArrayList<String>()

    init {
        initializePaths(FontsDefinitions.PATH_FORMAT)
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

    companion object {
        private const val FONT_FILE_TYPE: String = "ttf"
        private const val FONT_FOLDER_NAME: String = "fonts"
        private const val PATH_FORMAT = "$FONT_FOLDER_NAME/%s.$FONT_FILE_TYPE"
        private fun createParams(size: Int): FreetypeFontLoader.FreeTypeFontLoaderParameter {
            val params = FreetypeFontLoader.FreeTypeFontLoaderParameter()
            params.fontFileName = "$FONT_FOLDER_NAME/symtext.$FONT_FILE_TYPE"
            params.fontParameters.size = size
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

    }
}
