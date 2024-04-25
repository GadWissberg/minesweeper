package com.gadarts.minesweeper.systems

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.TexturesDefinitions

class GameUtils {
    companion object {
        fun createTileModel(
            modelBuilder: ModelBuilder,
            assetsManager: GameAssetManager,
            offset: Float = 0F,
            size: Float = 1F,
            addTextureAttribute: Boolean = true
        ): Model {
            var vertexAttributes = VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal
            val material = Material()
            if (addTextureAttribute) {
                val diffuse = TextureAttribute.createDiffuse(
                    assetsManager.getAssetByDefinition(
                        TexturesDefinitions.TILE_UNREVEALED
                    )
                )
                diffuse.scaleU = 1F
                diffuse.scaleV = 1F
                material.set(diffuse)
                vertexAttributes = vertexAttributes or VertexAttributes.Usage.TextureCoordinates
            }
            return modelBuilder.createRect(
                0F + offset, 0F, size + offset,
                size + offset, 0F, size + offset,
                size + offset, 0F, 0F + offset,
                0F + offset, 0F, 0F + offset,
                0F, 1F, 0F,
                material,
                (vertexAttributes).toLong()
            )
        }
    }

}
