package com.daniellemarsh.ujianbro.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.daniellemarsh.ujianbro.MainActivity
import com.daniellemarsh.ujianbro.extension.toast
import com.daniellemarsh.ujianbro.utils.NotificationUtils
import com.daniellemarsh.ujianbro.utils.Utils
import timber.log.Timber

class FGService: Service() {
	
	private val binder: IBinder = FGServiceBinder()
	
	override fun onCreate() {
		super.onCreate()
		
		startForeground(123, NotificationUtils.foregroundNotification(this))
	}
	
	override fun onBind(intent: Intent?): IBinder {
		return binder
	}
	
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
	
		return START_NOT_STICKY
	}
	
	inner class FGServiceBinder: Binder() {
		fun getService(): FGService {
			return this@FGService
		}
	}
	
}