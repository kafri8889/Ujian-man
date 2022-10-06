package com.daniellemarsh.ujianbro.common

import android.content.Context
import android.media.MediaPlayer
import com.daniellemarsh.ujianbro.R
import javax.inject.Inject

class AlertManager @Inject constructor(
	private val context: Context
) {
	
	private var mediaPlayer: MediaPlayer? = null
	
	init {
		mediaPlayer = MediaPlayer.create(context, R.raw.alert)
	}
	
	fun start() {
		if (mediaPlayer?.isPlaying == false) {
			mediaPlayer?.start()
		}
	}
	
	fun onDestroy() {
		mediaPlayer?.release()
		mediaPlayer = null
	}
	
}