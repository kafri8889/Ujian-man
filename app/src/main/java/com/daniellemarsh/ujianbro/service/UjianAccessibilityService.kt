package com.daniellemarsh.ujianbro.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import com.daniellemarsh.ujianbro.BuildConfig
import timber.log.Timber
import java.io.IOException

class UjianAccessibilityService: AccessibilityService() {
	
	override fun onAccessibilityEvent(event: AccessibilityEvent?) {
		if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			if (event.packageName != null && event.className != null) {
				val compName = ComponentName(
					event.packageName.toString(),
					event.className.toString()
				)
				
				Timber.i("winfo: ${compName.packageName}, ${compName.className}")
				
				if (compName.packageName != BuildConfig.APPLICATION_ID) {
					val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
						packageManager.getApplicationInfo(compName.packageName, PackageManager.ApplicationInfoFlags.of(0))
					} else {
						packageManager.getApplicationInfo(compName.packageName, 0)
					}
					
					val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) || (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0)
					
					Timber.i("is ${compName.packageName} system app? $isSystem")
					
					if (!isSystem) {
						try {
							forceCloseApp(compName.packageName)
						} catch (e: IOException) {
							Timber.i("Failed force close app ${compName.packageName}")
							e.printStackTrace()
						}
					}
				}
			}
		}
		
//		windows.forEach { windowInfo ->
//			val bounds = Rect()
//
//			windowInfo.getBoundsInScreen(bounds)
//
//			Timber.i("winfo: ${windowInfo.id}, $bounds")
//		}
	}
	
	override fun onServiceConnected() {
		super.onServiceConnected()
		serviceInfo.apply {
			eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
			feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
			flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
		}
		
		this.serviceInfo = serviceInfo
	}
	
	override fun onInterrupt() {
	
	}
	
	@Throws(IOException::class)
	fun forceCloseApp(packageName: String) {
//		val suProcess = Runtime.getRuntime().exec("adb shell am force-stop $packageName")
//		val os = DataOutputStream(suProcess.outputStream)
//
//		os.writeBytes("adb shell\n")
//		os.flush()
//
//		os.writeBytes("am force-stop $packageName\n")
//		os.flush()
	
//		val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
//		val runningApp = am.runningAppProcesses
//
//		Timber.i("App: ${runningApp.map { it.processName }}")
//		runningApp.forEach { proccessInfo ->
//			Timber.i("App: ${proccessInfo.processName}")
//
//			if (proccessInfo.processName.contains(packageName)) {
//				android.os.Process.sendSignal(proccessInfo.pid, android.os.Process.SIGNAL_KILL)
//				android.os.Process.killProcess(proccessInfo.pid)
//
//				Timber.i("App: ${proccessInfo.processName} killed!")
//			}
//		}
		
	}
	
}