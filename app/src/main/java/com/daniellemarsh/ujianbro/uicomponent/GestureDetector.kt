package com.daniellemarsh.ujianbro.uicomponent

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.daniellemarsh.ujianbro.extension.fraction
import com.daniellemarsh.ujianbro.utils.Utils.screenSize
import timber.log.Timber

@SuppressLint("ClickableViewAccessibility")
@Composable
fun GestureDetector(
	modifier: Modifier = Modifier,
	onAlert: () -> Unit
) {
	
	val context = LocalContext.current
	
	val (
		screenWidth,
		screenHeight
	) = remember { (context as Activity).screenSize() }
	
	AndroidView(
		factory = { ctx ->
			View(ctx).apply {
				layoutParams = ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT
				)
				
				setOnTouchListener { _, event ->
					Timber.i("coor: ${event.x}, ${event.y}")
					
					val x = event.x
					val y = event.y
					
					// width = 1075
					// min left with fraction 0.9 = 967.5
					// min right with fraction 0.9 = 107.5
					val minLeft = screenWidth.fraction(0.95f)
					val minRight = screenWidth - minLeft
					
					val minBottom = screenHeight.fraction(0.95f)
					val minTop = screenHeight - minBottom
					
					if (x < minRight || x > minLeft) {
						onAlert()
						Timber.i("Trigger alert from gesture!")
					}
					
					if (y < minTop || y > minBottom) {
						onAlert()
						Timber.i("Trigger alert from gesture!")
					}
					
					true
				}
			}
		},
		modifier = modifier
	)
}
