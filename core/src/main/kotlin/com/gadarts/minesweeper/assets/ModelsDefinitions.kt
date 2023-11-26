package com.gadarts.minesweeper.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.graphics.g3d.Model

enum class ModelsDefinitions(fileNames: Int = 1) : AssetDefinition<Model> {

    PIG;

    private val paths = ArrayList<String>()
    private val pathFormat = "models/%s.g3dj"

    init {
        initializePaths(pathFormat, fileNames)
    }

    override fun getPaths(): ArrayList<String> {
        return paths
    }

    override fun getParameters(): AssetLoaderParameters<Model>? {
        return null
    }

    override fun getClazz(): Class<Model> {
        return Model::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }

}
