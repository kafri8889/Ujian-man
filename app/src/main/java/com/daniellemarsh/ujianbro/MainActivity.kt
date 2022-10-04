package com.daniellemarsh.ujianbro

import android.content.ComponentName
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.*
import android.view.View
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.daniellemarsh.ujianbro.extension.toast
import com.daniellemarsh.ujianbro.service.FGService
import com.daniellemarsh.ujianbro.theme.UjianBroTheme
import com.daniellemarsh.ujianbro.ui.home.HomeListener
import com.daniellemarsh.ujianbro.ui.home.HomeScreen
import com.daniellemarsh.ujianbro.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : ComponentActivity(), ServiceConnection {
	
	private val homeViewModel: HomeViewModel by viewModels()
	
	private var fgService: FGService? = null
	
	private var mediaPlayer: MediaPlayer? = null
	
	private var wasOnStopCalledAfterOnWindowFocusChanged = false
	
	private var currentMediaJob: Job? = null
	
	@OptIn(ExperimentalFoundationApi::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
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
		
		mediaPlayer = MediaPlayer.create(this, R.raw.alert)
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			window.decorView.setOnApplyWindowInsetsListener { v, insets ->
				val mInsets = v.onApplyWindowInsets(insets)
				
				if (mInsets.isVisible(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())) {
					hideSystemBars()
					mediaPlayer?.start()
				}
				
				return@setOnApplyWindowInsetsListener mInsets
			}
		} else {
			window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
				if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
					hideSystemBars()
					mediaPlayer?.start()
				}
			}
		}
		
		homeViewModel.setListener(object : HomeListener {
			override fun alert() {
				mediaPlayer?.start()
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
	
	override fun onWindowFocusChanged(hasFocus: Boolean) {
		super.onWindowFocusChanged(hasFocus)
		
		// Focus gain? cancel jobnya
		if (hasFocus) {
			currentMediaJob?.cancel()
		}
		
		currentMediaJob = lifecycleScope.launch {
			// Focus loss?, bunyiin terus
			while (!hasFocus) {
				delay(1000)
				withContext(Dispatchers.Main) {
					mediaPlayer?.start()
				}
			}
		}
	}
	
	override fun onStop() {
		wasOnStopCalledAfterOnWindowFocusChanged = true
		super.onStop()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		
		mediaPlayer?.release()
		mediaPlayer = null
		
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
