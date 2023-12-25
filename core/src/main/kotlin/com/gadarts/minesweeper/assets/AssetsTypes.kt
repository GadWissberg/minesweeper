package com.gadarts.minesweeper.assets

enum class AssetsTypes(
    val assets: Array<out AssetDefinition<*>> = arrayOf()
) {
    MODELS(ModelsDefinitions.entries.toTypedArray()),
    TEXTURES(TexturesDefinitions.entries.toTypedArray()),
    SOUNDS(SoundsDefinitions.entries.toTypedArray()),
    FONTS(FontsDefinitions.entries.toTypedArray());

}
