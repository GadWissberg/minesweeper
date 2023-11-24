package com.gadarts.minesweeper.components

import com.badlogic.ashley.core.ComponentMapper


object ComponentsMappers {
    val modelInstance: ComponentMapper<ModelInstanceComponent> =
        ComponentMapper.getFor(ModelInstanceComponent::class.java)
}
