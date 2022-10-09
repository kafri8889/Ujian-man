package com.daniellemarsh.ujianbro.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsControllerCompat
import com.daniellemarsh.ujianbro.BuildConfig
import com.daniellemarsh.ujianbro.R
import com.daniellemarsh.ujianbro.extension.toast
import com.daniellemarsh.ujianbro.uicomponent.GestureDetector
import com.daniellemarsh.ujianbro.uicomponent.LoadingDialog
import com.daniellemarsh.ujianbro.uicomponent.NewVersionDialog
import com.daniellemarsh.ujianbro.uicomponent.RequestPermissionDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
@Composable
fun HomeScreen(
	viewModel: HomeViewModel
) {
	
	val context = LocalContext.current

	val scope = rememberCoroutineScope()
	val systemUiController = rememberSystemUiController()
	
	val readWritePermissionState = rememberMultiplePermissionsState(
		permissions = listOf(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
		)
	)
	
	val requestedUrl by viewModel.requestedUrl.collectAsState()
	val reloadWebView by viewModel.reloadWebView.collectAsState()
	val isDownloading by viewModel.isDownloading.collectAsState()
	val currentDownload by viewModel.currentDownload.collectAsState()
	val latestAppVersion by viewModel.latestAppVersion.collectAsState()
	val isNetworkHaveInternet by viewModel.isNetworkHaveInternet.collectAsState()
	val isThereANewestVersion by viewModel.isThereANewestVersion.collectAsState()
	
	var isWebViewLoaded by remember { mutableStateOf(false) }
	var isPermissionShouldShowRationale by remember { mutableStateOf(false) }
	
	SideEffect {
		systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
		systemUiController.isNavigationBarVisible = false
	}
	
	LaunchedEffect(viewModel) {
		viewModel.effect.collect { effect ->
			when (effect) {
				is HomeEffect.NetworkException -> {
					effect.message.toast(context)
				}
				is HomeEffect.DownloadLatestAppComplete -> {
				
				}
				else -> {}
			}
			
			viewModel.resetEffect()
		}
	}
	
	BackHandler {
		viewModel.alert()
	}
	
	Box(
		modifier = Modifier
			.fillMaxSize()
	) {
		AnimatedVisibility(
			visible = isPermissionShouldShowRationale,
			enter = fadeIn(
				animationSpec = tween(250)
			),
			exit = fadeOut(
				animationSpec = tween(250)
			),
			modifier = Modifier
				.zIndex(101f)
		) {
			RequestPermissionDialog(
				onDismissRequest = {
					isPermissionShouldShowRationale = false
				},
				onOpenSettingsClicked = {
					isPermissionShouldShowRationale = false
					
					viewModel.disallowAlert()
					
					val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
						data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
						flags = Intent.FLAG_ACTIVITY_NEW_TASK
					}
					
					context.startActivity(intent)
				}
			)
		}
		
		AnimatedVisibility(
			visible = isThereANewestVersion,
			enter = fadeIn(
				animationSpec = tween(250)
			),
			exit = fadeOut(
				animationSpec = tween(250)
			),
			modifier = Modifier
				.zIndex(100f)
		) {
			NewVersionDialog(
				isDownloading = isDownloading,
				downloadProgress = currentDownload.progress,
				latestVersionCode = latestAppVersion,
				onUpdateClicked = {
					if (readWritePermissionState.allPermissionsGranted) {
						viewModel.downloadLatestVersion()
					} else {
						isPermissionShouldShowRationale = true
					}
				},
				onDismissRequest = {
					viewModel.setIsThereANewestVersion(false)
				}
			)
		}
		
		AnimatedVisibility(
			visible = !isWebViewLoaded or !isNetworkHaveInternet,
			enter = fadeIn(
				animationSpec = tween(250)
			),
			exit = fadeOut(
				animationSpec = tween(250)
			),
			modifier = Modifier
				.zIndex(100f)
		) {
			LoadingDialog(isPlaying = !isWebViewLoaded or !isNetworkHaveInternet)
		}
		
		GestureDetector(
			onAlert = {
				viewModel.alert()
			},
			modifier = Modifier
				.fillMaxSize()
		)
		
		ActionButtons(
			onRefresh = {
				viewModel.setReloadWebView(true)
			},
			onExit = {
				viewModel.exit()
			},
			modifier = Modifier
				.padding(16.dp)
				.zIndex(1f)
				.align(Alignment.BottomEnd)
		)
		
		WebScreen(
			requestedUrl = requestedUrl,
			reloadWebView = reloadWebView,
			onReloadWebView = { reload ->
				viewModel.setReloadWebView(reload)
			},
			onWebViewLoadedChange = { isLoaded ->
				isWebViewLoaded = isLoaded
			},
			modifier = Modifier
				.fillMaxSize()
		)
	}

}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebScreen(
	requestedUrl: String,
	reloadWebView: Boolean,
	modifier: Modifier = Modifier,
	onReloadWebView: (reload: Boolean) -> Unit,
	onWebViewLoadedChange: (isLoaded: Boolean) -> Unit
) {
	
	Column(
		modifier = modifier
	) {
		Row(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth()
		) {
			Box(
				modifier = Modifier
					.padding(4.dp)
					.width(4.dp)
					.fillMaxHeight()
					.clip(CircleShape)
					.background(Color(0xFF8fd0f8))
			)
			
			Column(
				modifier = Modifier
					.weight(1f)
					.fillMaxHeight()
			) {
				Box(
					modifier = Modifier
						.padding(4.dp)
						.height(4.dp)
						.fillMaxWidth()
						.clip(CircleShape)
						.background(Color.White)
						.shadow(0.5.dp)
				)
				
				AndroidView(
					factory = { context ->
						WebView(context).apply {
							layoutParams = ViewGroup.LayoutParams(
								ViewGroup.LayoutParams.MATCH_PARENT,
								ViewGroup.LayoutParams.MATCH_PARENT
							)
							
							webViewClient = object : WebViewClient() {
								override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
									onWebViewLoadedChange(false)
								}
								
								override fun onPageFinished(view: WebView?, url: String?) {
									if (view?.progress == 100) {
										onWebViewLoadedChange(true)
									}
								}
							}
							
							webChromeClient = object : WebChromeClient() {}
							
							settings.apply {
								userAgentString =
									"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
								useWideViewPort = true
								javaScriptEnabled = true
								builtInZoomControls = true
								displayZoomControls = false
								loadWithOverviewMode = true
								
								setSupportZoom(true)
							}
							
							loadUrl(requestedUrl)
						}
					},
					update = {
						if (reloadWebView) {
							it.loadUrl(requestedUrl)
							onReloadWebView(false)
						}
					},
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f)
				)
				
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center,
					modifier = Modifier
						.fillMaxWidth()
				) {
					Image(
						painter = painterResource(id = R.drawable.ic_launcher),
						contentDescription = null,
						modifier = Modifier
							.size(24.dp)
					)
					
					Text(
						text = "UJIAN MAN 1 BOGOR",
						style = MaterialTheme.typography.titleSmall.copy(
							fontWeight = FontWeight.Bold
						)
					)
					
					Image(
						painter = painterResource(id = R.drawable.ic_launcher),
						contentDescription = null,
						modifier = Modifier
							.size(24.dp)
					)
				}
			}
			
			Box(
				modifier = Modifier
					.padding(4.dp)
					.width(4.dp)
					.fillMaxHeight()
					.clip(CircleShape)
					.background(Color(0xFF8fd0f8))
			)
		}
		
		Box(
			modifier = Modifier
				.padding(4.dp)
				.height(4.dp)
				.fillMaxWidth()
				.clip(CircleShape)
				.background(Color(0xFF8fd0f8))
		)
	}
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ActionButtons(
	modifier: Modifier = Modifier,
	onRefresh: () -> Unit,
	onExit: () -> Unit
) {
	
	val systemUiController = rememberSystemUiController()
	
	var isFabExpanded by remember { mutableStateOf(false) }
	var buttonExitVisible by remember { mutableStateOf(true) }
	var buttonRefreshVisible by remember { mutableStateOf(true) }
	
	val iconRotationDegree by animateFloatAsState(
		targetValue = if (isFabExpanded) 360f else 0f,
		animationSpec = tween(250)
	)
	
	Column(
		horizontalAlignment = Alignment.End,
		modifier = modifier
	) {
		Row {
			AnimatedVisibility(
				visible = buttonRefreshVisible and isFabExpanded,
				enter = scaleIn(tween(200)),
				exit = scaleOut(tween(200)),
				modifier = Modifier
					.padding(end = 16.dp)
			) {
				Button(
					shape = MaterialTheme.shapes.small,
					onClick = {
						buttonRefreshVisible = false
					}
				) {
					Text("REFRESH")
				}
			}
			
			AnimatedVisibility(
				visible = isFabExpanded,
				enter = scaleIn(tween(200)),
				exit = scaleOut(tween(200))
			) {
				FloatingActionButton(
					onClick = {
						onRefresh()
						isFabExpanded = false
					}
				) {
					Icon(
						painter = painterResource(id = R.drawable.ic_autorenew),
						contentDescription = null,
						modifier = Modifier
							.size(24.dp)
					)
				}
			}
		}
		
		Spacer(modifier = Modifier.height(16.dp))
		
		Row {
			AnimatedVisibility(
				visible = buttonExitVisible and isFabExpanded,
				enter = scaleIn(tween(200)),
				exit = scaleOut(tween(200)),
				modifier = Modifier
					.padding(end = 16.dp)
			) {
				Button(
					shape = MaterialTheme.shapes.small,
					onClick = {
						buttonExitVisible = false
					}
				) {
					Text("EXIT")
				}
			}
			
			AnimatedVisibility(
				visible = isFabExpanded,
				enter = scaleIn(tween(200)),
				exit = scaleOut(tween(200))
			) {
				FloatingActionButton(
					onClick = onExit
				) {
					Icon(
						imageVector = Icons.Rounded.Close,
						contentDescription = null
					)
				}
			}
		}
		
		Spacer(modifier = Modifier.height(16.dp))
		
		FloatingActionButton(
			onClick = {
				isFabExpanded = !isFabExpanded
				
				if (!isFabExpanded) {
					buttonExitVisible = true
					buttonRefreshVisible = true
				}
				
				when {
					systemUiController.isNavigationBarVisible or systemUiController.isStatusBarVisible -> {
						systemUiController.isNavigationBarVisible = false
						systemUiController.isStatusBarVisible = false
					}
				}
			}
		) {
			Icon(
				imageVector = if (isFabExpanded) Icons.Rounded.Home else Icons.Rounded.Add,
				contentDescription = null,
				modifier = Modifier
					.rotate(iconRotationDegree)
			)
		}
	}
}
