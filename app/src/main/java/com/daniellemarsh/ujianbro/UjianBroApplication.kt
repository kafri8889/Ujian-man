package com.daniellemarsh.ujianbro

import android.app.Application
import android.os.Build
import com.daniellemarsh.ujianbro.common.NotifManager
import com.daniellemarsh.ujianbro.utils.NotificationUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class UjianBroApplication : Application() {
	
	@Inject lateinit var notifManager: NotifManager
	
	override fun onCreate() {
		super.onCreate()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			NotificationUtils.createChannel(this)
			notifManager.createAllChannel()
		}
	}
}