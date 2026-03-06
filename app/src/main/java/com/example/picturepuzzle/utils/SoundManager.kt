package com.example.picturepuzzle.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.picturepuzzle.R

class SoundManager(context: Context) {

    private var soundPool: SoundPool? = null
    private var winSoundId: Int = 0
    private var clickSoundId: Int = 0
    private var isEnabled: Boolean = true

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sounds
        winSoundId = soundPool?.load(context, R.raw.win_sound, 1) ?: 0
        clickSoundId = soundPool?.load(context, R.raw.click_sound, 1) ?: 0
    }

    fun playWinSound() {
        if (isEnabled && winSoundId != 0) {
            soundPool?.play(winSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playClickSound() {
        if (isEnabled && clickSoundId != 0) {
            soundPool?.play(clickSoundId, 0.5f, 0.5f, 1, 0, 1f)
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}