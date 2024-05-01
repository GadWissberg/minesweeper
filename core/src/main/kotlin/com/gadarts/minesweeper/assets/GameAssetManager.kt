package com.gadarts.minesweeper.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import java.util.Arrays


open class GameAssetManager : AssetManager() {

    fun loadAssets() {
        initializeCustomLoaders()
        AssetsTypes.entries.forEach { type ->
            if (type.assets.isNotEmpty()) {
                type.assets.forEach { asset ->
                    asset.getPaths().forEach { path ->
                        if (asset.getParameters() != null) {
                            load(
                                path,
                                BitmapFont::class.java,
                                (asset.getParameters() as FreetypeFontLoader.FreeTypeFontLoaderParameter)
                            )
                        } else {
                            load(path, asset.getClazz())
                        }
                    }
                }
            }
        }
        finishLoading()
        generateModelsBoundingBoxes()
    }

    private fun generateModelsBoundingBoxes() {
        Arrays.stream(ModelsDefinitions.entries.toTypedArray())
            .forEach { def ->
                val definitionName = def.getDefinitionName()
                val model: Model = getAssetByDefinition(def)
                addAsset(
                    BOUNDING_BOX_PREFIX + definitionName,
                    BoundingBox::class.java,
                    model.calculateBoundingBox(BoundingBox())
                )
            }
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

    fun loadParticleEffects(billboardParticleBatch: BillboardParticleBatch) {
        ParticleEffectsDefinitions.entries.forEach {
            it.getPaths().forEach { _ ->
                load(
                    it.getPaths()[0],
                    ParticleEffect::class.java,
                    ParticleEffectLoader.ParticleEffectLoadParameter(
                        Array.with(
                            billboardParticleBatch
                        )
                    )
                )
            }
        }
        finishLoading()
    }

    fun getCachedBoundingBox(definition: ModelsDefinitions): BoundingBox {
        return auxBoundingBox.set(
            get(
                BOUNDING_BOX_PREFIX + definition.getDefinitionName(),
                BoundingBox::class.java
            )
        )
    }

    companion object {
        private const val BOUNDING_BOX_PREFIX = "box_"
        private val auxBoundingBox = BoundingBox()
    }
}
