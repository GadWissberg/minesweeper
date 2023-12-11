package com.gadarts.minesweeper.components

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect

class ParticleEffectComponent : GameComponent {
    lateinit var effect: ParticleEffect
        private set

    override fun reset() {
    }

    fun init(effect: ParticleEffect) {
        this.effect = effect
    }

}
