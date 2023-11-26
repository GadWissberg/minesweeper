package com.gadarts.minesweeper.assets

enum class AssetsTypes(
    val assets: Array<out AssetDefinition<*>> = arrayOf()
) {
    MODELS(ModelsDefinitions.values());

}
