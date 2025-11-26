package com.app.mathracer.audio   // o el package que uses

import android.content.Context
import android.media.MediaPlayer
import com.app.mathracer.R

object MusicManager {

    private var mediaPlayer: MediaPlayer? = null

    fun start(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.music).apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
            }
        }
        if (mediaPlayer?.isPlaying != true) {
            mediaPlayer?.start()
        }
    }

    fun setMusicVolume(volume: Float) {
        val v = volume.coerceIn(0f, 1f)  
        mediaPlayer?.setVolume(v, v)
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
