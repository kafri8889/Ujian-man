package com.daniellemarsh.ujianbro.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.daniellemarsh.ujianbro.MainActivity
import com.daniellemarsh.ujianbro.R

/**
 * Unused
 */
object NotificationUtils {
	
	private const val channelID = "fg_notification"
	private const val channelName = "Foreground Service"
	
	@RequiresApi(Build.VERSION_CODES.O)
	fun createChannel(context: Context) {
		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			channel.setAllowBubbles(false)
		}
		
		channel.enableLights(false)
		channel.setBypassDnd(true)
		
		notificationManager.createNotificationChannel(channel)
	}
	
	@Suppress("deprecation")
	fun foregroundNotification(context: Context): Notification {
		val pi = PendingIntent.getActivity(
			context,
			123,
			Intent(context, MainActivity::class.java),
			PendingIntent.FLAG_IMMUTABLE
		)
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Notification.Builder(context, channelID)
				.setContentTitle("Ujian coock")
				.setContentText("Jangan di kill, kill klo udah slse")
				.setCategory(Notification.CATEGORY_SERVICE)
				.setContentIntent(pi)
				.build()
		} else {
			Notification.Builder(context)
				.setContentTitle("Ujian coock")
				.setContentText("Jangan di kill, kill klo udah slse")
				.setContentIntent(pi)
				.setCategory(Notification.CATEGORY_SERVICE)
				.build()
		}
	}
	
	fun simpleNotification(context: Context, title: String, text: String): Notification {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Notification.Builder(context, channelID)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setContentText(text)
				.setCategory(Notification.CATEGORY_STATUS)
				.build()
		} else {
			Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setContentText(text)
				.setCategory(Notification.CATEGORY_STATUS)
				.build()
		}
	}
	
}