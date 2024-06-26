package com.gadarts.minesweeper.systems.data

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.utils.Disposable
import com.gadarts.minesweeper.components.player.PowerupType


class PlayerData : Disposable {

    var invulnerableDisplay: Entity? = null
    val invulnerableEffectModel: Model = ModelBuilder().createSphere(
        2F,
        2.5F,
        2F,
        16,
        16,
        Material(ColorAttribute.createDiffuse(Color.GOLD), BlendingAttribute(1F)),
        (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
    )
    var invulnerableEffect: Float = 0F
    var invulnerableStepsLeft: Int = 0

    lateinit var digit: Entity
    val powerups: MutableMap<PowerupType, Int> = mutableMapOf()
    var player: Entity? = null

    init {
        reset()
    }

    fun reset() {
        invulnerableStepsLeft = 0
        PowerupType.entries.forEach { powerups[it] = 0 }
    }

    override fun dispose() {
        invulnerableEffectModel.dispose()
    }

}
