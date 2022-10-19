package com.daniellemarsh.ujianbro.service

import android.app.NotificationManager
import android.content.Context
import com.daniellemarsh.ujianbro.utils.NotificationUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FCMService: FirebaseMessagingService() {
	
	override fun onMessageReceived(message: RemoteMessage) {
		
		message.notification?.let { notification ->
			val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			
			notifManager.notify(
				Random(System.currentTimeMillis()).nextInt(),
				NotificationUtils.simpleNotification(
					context = this,
					title = notification.title ?: "",
					text = notification.body ?: ""
				)
			)
		}
		
		super.onMessageReceived(message)
	}
	
	override fun onNewToken(token: String) {
		super.onNewToken(token)
	}
}