package com.gadarts.minesweeper.components

import com.badlogic.ashley.core.ComponentMapper


object ComponentsMappers {
    val modelInstance: ComponentMapper<ModelInstanceComponent> =
        ComponentMapper.getFor(ModelInstanceComponent::class.java)
    val independentParticleEffect: ComponentMapper<IndependentParticleEffectComponent> =
        ComponentMapper.getFor(IndependentParticleEffectComponent::class.java)
    val followersParticleEffect: ComponentMapper<FollowerParticleEffectComponent> =
        ComponentMapper.getFor(FollowerParticleEffectComponent::class.java)
    val physics: ComponentMapper<PhysicsComponent> =
        ComponentMapper.getFor(PhysicsComponent::class.java)
    val crate: ComponentMapper<CrateComponent> =
        ComponentMapper.getFor(CrateComponent::class.java)
}
