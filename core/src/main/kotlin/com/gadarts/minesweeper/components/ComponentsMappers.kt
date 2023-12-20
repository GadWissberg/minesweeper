package com.gadarts.minesweeper.components

import com.badlogic.ashley.core.ComponentMapper


object ComponentsMappers {
    val modelInstance: ComponentMapper<ModelInstanceComponent> =
        ComponentMapper.getFor(ModelInstanceComponent::class.java)
    val particleEffect: ComponentMapper<ParticleEffectComponent> =
        ComponentMapper.getFor(ParticleEffectComponent::class.java)
    val physics: ComponentMapper<PhysicsComponent> =
        ComponentMapper.getFor(PhysicsComponent::class.java)
}
