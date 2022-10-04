package com.daniellemarsh.ujianbro

import android.app.Application
import android.os.Build
import com.daniellemarsh.ujianbro.utils.NotificationUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UjianBroApplication : Application() {
	
	override fun onCreate() {
		super.onCreate()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			NotificationUtils.createChannel(this)
		}
	}
}