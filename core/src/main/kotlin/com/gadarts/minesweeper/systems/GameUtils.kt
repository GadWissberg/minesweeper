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
            horizontalOffset: Float = 0F,
            size: Float = 1F
        ): Model {
            val diffuse = TextureAttribute.createDiffuse(
                assetsManager.getAssetByDefinition(
                    TexturesDefinitions.TILE_UNREVEALED
                )
            )
            diffuse.scaleU = 1F
            diffuse.scaleV = 1F
            return modelBuilder.createRect(
                0F + horizontalOffset, 0F, size + horizontalOffset,
                size + horizontalOffset, 0F, size + horizontalOffset,
                size + horizontalOffset, 0F, 0F + horizontalOffset,
                0F + horizontalOffset, 0F, 0F + horizontalOffset,
                0F, 1F, 0F,
                Material(
                    diffuse
                ),
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()
            )
        }
    }

}
