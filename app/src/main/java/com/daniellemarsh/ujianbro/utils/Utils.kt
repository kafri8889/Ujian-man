package com.daniellemarsh.ujianbro.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics


object Utils {
	
	fun Activity.screenSize(): Pair<Int, Int> {
		return resources.displayMetrics.widthPixels to resources.displayMetrics.heightPixels
	}
	
}
