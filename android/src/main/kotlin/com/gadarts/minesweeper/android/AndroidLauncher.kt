package com.gadarts.minesweeper.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.gadarts.minesweeper.MineSweeper

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val androidApplicationConfiguration = AndroidApplicationConfiguration()
        androidApplicationConfiguration.numSamples = 2
        initialize(MineSweeper(), androidApplicationConfiguration.apply {
            useImmersiveMode = true
        })
    }
}
