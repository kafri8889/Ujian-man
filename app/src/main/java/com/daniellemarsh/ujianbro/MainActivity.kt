package com.daniellemarsh.ujianbro

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.daniellemarsh.ujianbro.common.AlertManager
import com.daniellemarsh.ujianbro.extension.fraction
import com.daniellemarsh.ujianbro.extension.toast
import com.daniellemarsh.ujianbro.service.FGService
import com.daniellemarsh.ujianbro.theme.UjianBroTheme
import com.daniellemarsh.ujianbro.ui.home.HomeListener
import com.daniellemarsh.ujianbro.ui.home.HomeScreen
import com.daniellemarsh.ujianbro.ui.home.HomeViewModel
import com.daniellemarsh.ujianbro.utils.Utils
import com.daniellemarsh.ujianbro.utils.Utils.screenSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity(), ServiceConnection {
	
	@Inject lateinit var alertManager: AlertManager
	
	private lateinit var gestureDetector: GestureDetector
	
	private val homeViewModel: HomeViewModel by viewModels()
	
	private var fgService: FGService? = null
	
	private var currentMediaJob: Job? = null
	
	private var isActivityRunningInForeground = false
	private var fromExitButton = false
	
	@OptIn(ExperimentalFoundationApi::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
		setContent {
			UjianBroTheme(
				darkTheme = false
			) {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = Color.White
				) {
					CompositionLocalProvider(
						LocalOverscrollConfiguration provides null
					) {
						HomeScreen(
							viewModel = homeViewModel
						)
					}
				}
			}
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			window.decorView.setOnApplyWindowInsetsListener { v, insets ->
				val mInsets = v.onApplyWindowInsets(insets)
				
				if (mInsets.isVisible(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())) {
					hideSystemBars()
					alertManager.start()
				}
				
				return@setOnApplyWindowInsetsListener mInsets
			}
		} else {
			window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
				if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
					hideSystemBars()
					alertManager.start()
				}
			}
		}
		
//		window.decorView.filterTouchesWhenObscured = true
		window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
		
		gestureDetector = GestureDetector(this, object : GestureDetector.OnGestureListener {
			override fun onDown(e: MotionEvent): Boolean {
				return false
			}
			
			override fun onShowPress(e: MotionEvent) {
				return
			}
			
			override fun onSingleTapUp(e: MotionEvent): Boolean {
				return false
			}
			
			override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
				Timber.i("coor: (${e1.x}, ${e1.y}), (${e2.x}, ${e2.y})")
				
				val x = e2.x
				val y = e2.y
				
				val (width, height) = screenSize()
				
				// width = 1075
				// min left with fraction 0.9 = 967.5
				// min right with fraction 0.9 = 107.5
				val minLeft = width.fraction(0.97f)
				val minRight = width - minLeft
				
				if (x < minRight || x > minLeft) {
					alertManager.start()
				}
				
				return true
			}
			
			override fun onLongPress(e: MotionEvent) {
				return
			}
			
			override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
				return false
			}
		})
		
		homeViewModel.setListener(object : HomeListener {
			override fun exit() {
				fromExitButton = true
				finish()
			}
			
			override fun alert() {
				alertManager.start()
			}
		})
		
//		val serviceIntent = Intent(this, FGService::class.java)
//
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//			startForegroundService(serviceIntent)
//		} else startService(serviceIntent)
//
//		bindService(
//			serviceIntent,
//			this,
//			BIND_AUTO_CREATE
//		)
	}
	
//	override fun onTouchEvent(event: MotionEvent): Boolean {
//		return if (gestureDetector.onTouchEvent(event)) {
//			true
//		}
//		else {
//			super.onTouchEvent(event)
//		}
//	}
	
	override fun onWindowFocusChanged(hasFocus: Boolean) {
		super.onWindowFocusChanged(hasFocus)
		
		// Focus gain? cancel jobnya
		if (hasFocus) {
			currentMediaJob?.cancel()
		} else {
			if (!fromExitButton) {
				startActivity(
					Intent(this@MainActivity, MainActivity::class.java).apply {
						addCategory(Intent.CATEGORY_LAUNCHER)
					}
				)
			}
		}
		
		currentMediaJob = lifecycleScope.launch {
			// Focus loss?, bunyiin terus
			while (!hasFocus) {
				delay(900)
				withContext(Dispatchers.Main) {
//					if (!isActivityRunningInForeground) {
//						fgService?.po()
//					}
					
					alertManager.start()
				}
			}
		}
	}
	
	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
	}
	
	override fun onStart() {
		super.onStart()
		isActivityRunningInForeground = true
	}
	
	override fun onStop() {
		super.onStop()
		isActivityRunningInForeground = false
	}
	
	override fun onDestroy() {
		super.onDestroy()
		
		alertManager.onDestroy()
		
		try {
			unbindService(this)
		} catch (e: IllegalArgumentException) {
			Timber.e(e, "Service not registered")
		}
	}
	
	override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
		val binder = service as FGService.FGServiceBinder
		
		fgService = binder.getService()
	}
	
	override fun onServiceDisconnected(name: ComponentName?) {
		fgService = null
	}
	
	private fun hideSystemBars() {
		val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
		// Configure the behavior of the hidden system bars
//		windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
		// Hide both the status bar and the navigation bar
		windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
	}
	
}
