package com.daniellemarsh.ujianbro.common

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.daniellemarsh.ujianbro.R
import javax.inject.Inject

class NotifManager @Inject constructor(private val context: Context) {
	
	private var alertCount = 0
	
	private val notificationManager: NotificationManager
		get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

	@RequiresApi(Build.VERSION_CODES.O)
	fun createAllChannel() {
		val alertChannel = NotificationChannel(
			ALERT_COUNTER_CHANNEL_ID,
			ALERT_COUNTER_CHANNEL_NAME,
			NotificationManager.IMPORTANCE_HIGH
		).apply {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				setAllowBubbles(false)
			}
			
			enableLights(false)
			setBypassDnd(true)
			setShowBadge(false)
		}
		
		notificationManager.createNotificationChannel(alertChannel)
	}
	
	fun incrementAlertCount() {
		alertCount += 1
		
		// custom view?
		val notification = when {
			Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
				Notification.Builder(context, ALERT_COUNTER_CHANNEL_ID)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("Alert counter")
					.setContentText("count: $alertCount")
					.setCategory(Notification.CATEGORY_SERVICE)
					.setAutoCancel(true)
					.setCategory(Notification.CATEGORY_STATUS)
					.build()
			}
			Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
				Notification.Builder(context)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("Alert counter")
					.setContentText("count: $alertCount")
					.setCategory(Notification.CATEGORY_SERVICE)
					.setAutoCancel(true)
					.setCategory(Notification.CATEGORY_STATUS)
					.build()
			}
			else -> {
				Notification.Builder(context)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("Alert counter")
					.setContentText("count: $alertCount")
					.setCategory(Notification.CATEGORY_SERVICE)
					.setAutoCancel(true)
					.setCategory(Notification.CATEGORY_STATUS)
					.build()
			}
		}
		
		notificationManager.notify(ALERT_NOTIF_ID, notification)
	}
	
	companion object {
		const val ALERT_NOTIF_ID = 310804
		const val ALERT_COUNTER_CHANNEL_ID = "alert_counter"
		const val ALERT_COUNTER_CHANNEL_NAME = "Alert counter"
	}

}