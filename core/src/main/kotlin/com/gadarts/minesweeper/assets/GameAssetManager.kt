package com.gadarts.minesweeper.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader


open class GameAssetManager : AssetManager() {

    fun loadAssets() {
        initializeCustomLoaders()
        AssetsTypes.entries.forEach { type ->
            if (type.assets.isNotEmpty()) {
                type.assets.forEach { asset ->
                    asset.getPaths().forEach { load(it, asset.getClazz()) }
                }
            }
        }
        finishLoading()
    }

    private fun initializeCustomLoaders() {
        val resolver: FileHandleResolver = InternalFileHandleResolver()
        setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        val loader = FreetypeFontLoader(resolver)
        setLoader(BitmapFont::class.java, "ttf", loader)
    }

    inline fun <reified T> getAssetByDefinition(definition: AssetDefinition<T>): T {
        return get(definition.getPaths().random(), T::class.java)
    }

}
