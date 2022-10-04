package com.daniellemarsh.ujianbro.extension

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

fun Any?.toast(context: Context, length: Int = Toast.LENGTH_SHORT) {
	Handler(Looper.getMainLooper()).post {
		Toast.makeText(context, this.toString(), length).show()
	}
}

@SuppressLint("ComposableNaming")
@Composable
fun Any?.toast(length: Int = Toast.LENGTH_SHORT) {
	Toast.makeText(LocalContext.current, this.toString(), length).show()
}
