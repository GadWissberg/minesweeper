package com.gadarts.minesweeper

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.MathUtils.randomBoolean
import com.gadarts.minesweeper.assets.GameAssetManager
import com.gadarts.minesweeper.assets.SoundsDefinitions


class SoundPlayer(private val assetManager: GameAssetManager) {

    private var sfxEnabled = false


    init {
        sfxEnabled = GameDebugSettings.SFX_ENABLED
    }

    fun playSoundByDefinition(def: SoundsDefinitions) {
        if (!sfxEnabled) return
        val pitch: Float =
            1 + (if (def.randomPitch) (if (randomBoolean()) 1 else -1) else 0) * random(
                -PITCH_OFFSET,
                PITCH_OFFSET
            )
        val sound = assetManager.get<Sound>(getRandomSound(def))
        playSound(sound, 1F, pitch)
    }

    fun playSound(sound: Sound, volume: Float = 1F) {
        playSound(
            sound, volume, 1 + ((if (randomBoolean()) 1 else -1)) * random(
                -PITCH_OFFSET,
                PITCH_OFFSET
            )
        )
    }

    private fun playSound(sound: Sound, volume: Float, pitch: Float) {
        sound.play(volume, pitch, 0F)
    }

    private fun getRandomSound(soundDef: SoundsDefinitions): String {
        val paths = soundDef.getPaths()
        val filePath: String
        val random: Int = random(paths.size - 1)
        filePath = paths[random]
        return filePath
    }

    companion object {
        private const val PITCH_OFFSET = 0.1f
    }
}
