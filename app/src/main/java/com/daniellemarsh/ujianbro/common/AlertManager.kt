package com.daniellemarsh.ujianbro.common

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import com.daniellemarsh.ujianbro.R
import javax.inject.Inject

class AlertManager @Inject constructor(
	private val context: Context
) {
	
	private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
	
	private var listener: AlertListener? = null
	private var mediaPlayer: MediaPlayer? = null
	
	var lastCaller = ""
	var allowAlert = true
	
	init {
		mediaPlayer = MediaPlayer.create(context, R.raw.alert)
	}
	
	fun start() {
		val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
		
//		audioManager.setStreamVolume(
//			AudioManager.STREAM_MUSIC,
//			maxVolume,
//			AudioManager.FLAG_PLAY_SOUND
//		)
		
		if (allowAlert) {
			mediaPlayer?.start()
			listener?.onAlert()
		}
	}
	
	fun setListener(l: AlertListener) {
		listener = l
	}
	
	fun onDestroy() {
		mediaPlayer?.release()
		mediaPlayer = null
	}
	
	fun allowAlert(caller: String, force: Boolean = false) {
		when {
			force -> allowAlert = true
			caller.isEmpty() -> allowAlert = true
			caller == lastCaller -> allowAlert = true
		}
	}
	
	interface AlertListener {
		
		fun onAlert()
	}
	
}