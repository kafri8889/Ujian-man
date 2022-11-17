package com.daniellemarsh.ujianbro.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.ViewGroup
import android.webkit.*
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
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsControllerCompat
import com.daniellemarsh.ujianbro.BuildConfig
import com.daniellemarsh.ujianbro.R
import com.daniellemarsh.ujianbro.extension.toast
import com.daniellemarsh.ujianbro.uicomponent.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
@Composable
fun HomeScreen(
	viewModel: HomeViewModel
) {
	
	val view = LocalView.current
	val context = LocalContext.current

	val scope = rememberCoroutineScope()
	val systemUiController = rememberSystemUiController()
	
	val postNotifPermissionState = rememberPermissionState(
		permission = Manifest.permission.POST_NOTIFICATIONS
	)
	
	val readWritePermissionState = rememberMultiplePermissionsState(
		permissions = listOf(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
		)
	)
	
	val isTimeout by viewModel.isTimeout.collectAsState()
	val requestedUrl by viewModel.requestedUrl.collectAsState()
	val exitPassword by viewModel.exitPassword.collectAsState()
	val reloadWebView by viewModel.reloadWebView.collectAsState()
	val isDownloading by viewModel.isDownloading.collectAsState()
	val currentDownload by viewModel.currentDownload.collectAsState()
	val latestAppVersion by viewModel.latestAppVersion.collectAsState()
	val isBluetoothEnabled by viewModel.isBluetoothEnabled.collectAsState()
	val isNetworkHaveInternet by viewModel.isNetworkHaveInternet.collectAsState()
	val isThereANewestVersion by viewModel.isThereANewestVersion.collectAsState()
	
	val snackbarTimeout = remember(context, view) {
		Snackbar.make(
			view,
			"Waktu koneksi habis, periksa internet anda!, ada kuota?, atau lag?",
			Snackbar.LENGTH_LONG
		).apply {
			setAction(
				"Tutup"
			) {
				dismiss()
			}
		}
	}
	
	var isWebViewLoaded by remember { mutableStateOf(false) }
	var isErrorPageShowed by remember { mutableStateOf(false) }
	var isDesktopModeEnable by remember { mutableStateOf(false) }
	var isExitDialogShowing by remember { mutableStateOf(false) }
	var isPermissionShouldShowRationale by remember { mutableStateOf(false) }
	var isPostNotifPermissionShouldShowRationale by remember { mutableStateOf(true) }
	
	SideEffect {
		systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
		systemUiController.isSystemBarsVisible = false
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !postNotifPermissionState.status.isGranted) {
			postNotifPermissionState.launchPermissionRequest()
		}
	}
	
	LaunchedEffect(viewModel) {
		viewModel.effect.collect { effect ->
			when (effect) {
				is HomeEffect.NetworkException -> {
					effect.message.toast(context)
				}
				is HomeEffect.DownloadLatestAppComplete -> {
					"Download complete".toast(context)
				}
				is HomeEffect.BlankLatestAppVersionUrl -> {
					"Url not found".toast(context)
				}
				is HomeEffect.NullUrl -> {
					"Url Null!".toast(context)
				}
				else -> {}
			}
			
			viewModel.resetEffect()
		}
	}
	
	LaunchedEffect(isBluetoothEnabled) {
		when {
			isBluetoothEnabled -> viewModel.disallowAlert()
			else -> viewModel.allowAlert()
		}
	}
	
	LaunchedEffect(isTimeout) {
		Timber.i("runnig timot? $isTimeout")
		if (isTimeout and !snackbarTimeout.isShown) {
			snackbarTimeout.show()
		}
	}
	
	LaunchedEffect(isErrorPageShowed) {
		withContext(Dispatchers.Main) {
			if (isErrorPageShowed) {
				viewModel.disallowAlert()
				viewModel.disableSecurity()
			} else viewModel.enableSecurity()
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
			visible = isExitDialogShowing,
			enter = fadeIn(
				animationSpec = tween(250)
			),
			exit = fadeOut(
				animationSpec = tween(250)
			),
			modifier = Modifier
				.zIndex(104f)
		) {
			ExitDialog(
				correctPassword = exitPassword,
				onDismissRequest = {
					isExitDialogShowing	= false
				},
				onExit = {
					viewModel.exit()
				}
			)
//			AlertDialog(
//				onDismissRequest = {
//					isExitDialogShowing	= false
//				},
//				title = {
//					Text("Keluar")
//				},
//				text = {
//					OutlinedTextField(
//						value = exitPasswordUserInput,
//						keyboardOptions = KeyboardOptions(
//							keyboardType = KeyboardType.Number
//						),
//						onValueChange = { s ->
//							exitPasswordUserInput = s
//						},
//						label = {
//							Text("Masukkan password")
//						},
//						modifier = Modifier
//							.fillMaxWidth()
//					)
//				},
//				dismissButton = {
//					TextButton(
//						onClick = {
//							isExitDialogShowing = false
//						}
//					) {
//						Text("Batalkan")
//					}
//				},
//				confirmButton = {
//					Button(
//						onClick = {
//							viewModel.exit()
//						}
//					) {
//						Text("Keluar")
//					}
//				}
//			)
		}
		
		AnimatedVisibility(
			visible = isBluetoothEnabled,
			enter = fadeIn(
				animationSpec = tween(250)
			),
			exit = fadeOut(
				animationSpec = tween(250)
			),
			modifier = Modifier
				.zIndex(103f)
		) {
			AlertDialog(
				onDismissRequest = {},
				title = {
					Text("Bluetooth")
				},
				text = {
					Text("Bluetooth tidak boleh dinyalakan, matikan bluetooth untuk menghilangkan dialog ini!")
				},
				confirmButton = {}
			)
		}
		
		AnimatedVisibility(
			visible = !postNotifPermissionState.status.isGranted and isPostNotifPermissionShouldShowRationale,
			enter = fadeIn(
				animationSpec = tween(250)
			),
			exit = fadeOut(
				animationSpec = tween(250)
			),
			modifier = Modifier
				.zIndex(102f)
		) {
//			AlertDialog(
//				onDismissRequest = {
//					isPostNotifPermissionShouldShowRationale = false
//				},
//				title = {
//					Text("Request Permission")
//				},
//				text = {
//					Text(
//						text = "Aplikasi ini membutuhkan izin \"POST_NOTIFICATIONS\" untuk menampilkan notifikasi " +
//								"saat ada update-an terbaru"
//					)
//				},
//				confirmButton = {
//					Button(
//						onClick = {
//							isPostNotifPermissionShouldShowRationale = false
//
//							viewModel.disallowAlert()
//
//							val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//								data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//								flags = Intent.FLAG_ACTIVITY_NEW_TASK
//							}
//
//							context.startActivity(intent)
//						}
//					) {
//						Text("Buka pengaturan")
//					}
//				},
//				dismissButton = {
//					TextButton(
//						onClick = {
//							isPostNotifPermissionShouldShowRationale = false
//						}
//					) {
//						Text("Tutup")
//					}
//				}
//			)
			RequestPostNotifPermissionDialog(
				onDismissRequest = {
					isPostNotifPermissionShouldShowRationale = false
				},
				onOpenSettingsClicked = {
					isPostNotifPermissionShouldShowRationale = false
					
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
//			AlertDialog(
//				onDismissRequest = {
//					isPermissionShouldShowRationale = false
//				},
//				title = {
//					Text("Request Permission")
//				},
//				text = {
//					Text(
//						text = "Aplikasi ini membutuhkan izin \"WRITE_EXTERNAL_STORAGE\" untuk menyimpan " +
//								"update-an terbaru yang sudah di download"
//					)
//				},
//				confirmButton = {
//					Button(
//						onClick = {
//							isPermissionShouldShowRationale = false
//
//							viewModel.disallowAlert()
//
//							val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//								data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//								flags = Intent.FLAG_ACTIVITY_NEW_TASK
//							}
//
//							context.startActivity(intent)
//						}
//					) {
//						Text("Buka pengaturan")
//					}
//				},
//				dismissButton = {
//					TextButton(
//						onClick = {
//							isPostNotifPermissionShouldShowRationale = false
//						}
//					) {
//						Text("Tutup")
//					}
//				}
//			)
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
			visible = BuildConfig.VERSION_CODE < latestAppVersion,
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
//					viewModel.setIsThereANewestVersion(false)
				},
				onExit = {
					viewModel.exit()
				}
			)
		}
		
		AnimatedVisibility(
			visible = !isWebViewLoaded and isNetworkHaveInternet and !isErrorPageShowed,
			enter = fadeIn(
				animationSpec = tween(250)
			),
			exit = fadeOut(
				animationSpec = tween(250)
			),
			modifier = Modifier
				.zIndex(100f)
		) {
			LoadingDialog(
				isPlaying = !isWebViewLoaded and isNetworkHaveInternet and !isErrorPageShowed,
				onExitClicked = {
					when {
						isTimeout -> viewModel.exit()
						isErrorPageShowed -> viewModel.exit()
						isNetworkHaveInternet -> isExitDialogShowing = true
						else -> viewModel.exit()
					}
				}
			)
		}
		
		GestureDetector(
			onAlert = {
				viewModel.alert()
			},
			modifier = Modifier
				.fillMaxSize()
		)
		
		ActionButtons(
			desktopModeEnable = isDesktopModeEnable,
			onDesktopMode = { enable ->
				isDesktopModeEnable = enable
			},
			onRefresh = {
				viewModel.setReloadWebView(true)
			},
			onExit = {
				when {
					isTimeout -> viewModel.exit()
					isErrorPageShowed -> viewModel.exit()
					isNetworkHaveInternet -> isExitDialogShowing = true
					else -> viewModel.exit()
				}
			},
			modifier = Modifier
				.padding(16.dp)
				.zIndex(1f)
				.align(Alignment.BottomEnd)
		)
		
		WebScreen(
			requestedUrl = requestedUrl,
			reloadWebView = reloadWebView,
			isDesktopModeEnable = isDesktopModeEnable,
			isNetworkAvailable = isNetworkHaveInternet,
			onReloadWebView = { reload ->
				viewModel.setReloadWebView(reload)
			},
			onWebViewLoadedChange = { isLoaded ->
				isWebViewLoaded = isLoaded and requestedUrl.isNotBlank()
			},
			onError = { timeout ->
				viewModel.setTimeout(timeout)
			},
			onErrorPageShowed = { showed ->
				isErrorPageShowed = showed
			},
			modifier = Modifier
				.fillMaxSize()
		)
	}

}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebScreen(
	requestedUrl: String,
	reloadWebView: Boolean,
	isDesktopModeEnable: Boolean,
	isNetworkAvailable: Boolean,
	modifier: Modifier = Modifier,
	onError: (timeout: Boolean) -> Unit,
	onReloadWebView: (reload: Boolean) -> Unit,
	onErrorPageShowed: (show: Boolean) -> Unit,
	onWebViewLoadedChange: (isLoaded: Boolean) -> Unit
) {
	
	val config = LocalConfiguration.current
	val context = LocalContext.current
	
	var isWebViewError by remember { mutableStateOf(false) }
	
	Column(
		modifier = modifier
	) {
		AnimatedContent(targetState = isNetworkAvailable and !isWebViewError) { available ->
			LaunchedEffect(available) {
				onErrorPageShowed(!available)
			}
			
			if (available) {
				Column {
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
//								.horizontalScroll(rememberScrollState())
//								.verticalScroll(rememberScrollState())
//								.widthIn(max = config.smallestScreenWidthDp.dp * 4)
//								.heightIn(max = config.screenHeightDp.dp * 2f)
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
											ViewGroup.LayoutParams.WRAP_CONTENT,
											ViewGroup.LayoutParams.WRAP_CONTENT
										)
										
										webViewClient = object : WebViewClient() {
											override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
												if (url?.contains("file://") != true) {
													isWebViewError = false
													Timber.i("urel bukan urel error")
												}
												Timber.i("urel: $url")
												onWebViewLoadedChange(false)
											}
											
											override fun onPageFinished(view: WebView?, url: String?) {
												Timber.i("prog: ${view?.progress}")
												
												if (isWebViewError && url?.contains("file://") != true) {
													view?.loadUrl("file:///android_asset/weberror.html")
													Timber.i("urel set ke: file:///android_asset/weberror.html")
												}
												
												if (url?.contains("file://") == true) {
													view?.stopLoading()
													view?.loadUrl(requestedUrl)
												}
												
												if (view?.progress == 100) {
													onWebViewLoadedChange(true)
													onError(false)
												}
											}
											
											override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
												if (!isWebViewError) {
													try {
														view?.stopLoading()
													} catch (e: Exception) {}
													
													try {
														view?.loadUrl("about:blank")
													} catch (e: Exception) {}
													
													view?.loadUrl("file:///android_asset/weberror.html")
													isWebViewError = true
													onError(true)
												}
											}
											
//											override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
//												try {
//													view?.stopLoading()
//												} catch (e: Exception) {}
//
//												try {
//													view?.loadUrl("about:blank")
//												} catch (e: Exception) {}
//
//												view?.loadUrl("file:///android_asset/weberror.html")
//												isWebViewError = true
//												onError(true)
//											}
//
//											override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
//												try {
//													view?.stopLoading()
//												} catch (e: Exception) {}
//
//												try {
//													view?.loadUrl("about:blank")
//												} catch (e: Exception) {}
//
//												view?.loadUrl("file:///android_asset/weberror.html")
//												isWebViewError = true
//												onError(true)
//											}
										}
										
										webChromeClient = object : WebChromeClient() {}
										
										settings.apply {
											userAgentString =
												"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36"
											useWideViewPort = false
											javaScriptEnabled = true
											builtInZoomControls = true
											displayZoomControls = false
											loadWithOverviewMode = true
											allowContentAccess = true
											domStorageEnabled = true
//
											setSupportZoom(true)
										}
										
										setInitialScale(0)
										
										loadUrl(requestedUrl)
									}
								},
								update = {
									it.setInitialScale(
										if (isDesktopModeEnable) 100
										else 0
									)
									
									if (reloadWebView) {
										it.stopLoading()
										it.loadUrl("about:blank")
										it.loadUrl(requestedUrl)
										Timber.i("rilot $requestedUrl")
										onReloadWebView(false)
									}
								},
								modifier = Modifier
									.fillMaxWidth()
									.fillMaxHeight()
//									.weight(1f)
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
			} else {
				Image(
					painter = painterResource(id = R.drawable.no_internet_ui),
					contentDescription = null,
					contentScale = ContentScale.FillBounds,
					modifier = Modifier
						.fillMaxSize()
				)
			}
		}
	}
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ActionButtons(
	desktopModeEnable: Boolean,
	modifier: Modifier = Modifier,
	onDesktopMode: (enable: Boolean) -> Unit,
	onRefresh: () -> Unit,
	onExit: () -> Unit
) {
	
	val systemUiController = rememberSystemUiController()
	
	var isFabExpanded by remember { mutableStateOf(false) }
	var buttonExitVisible by remember { mutableStateOf(true) }
	var buttonRefreshVisible by remember { mutableStateOf(true) }
	var buttonDesktopModeVisible by remember { mutableStateOf(true) }
	
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
				visible = buttonDesktopModeVisible and isFabExpanded,
				enter = scaleIn(tween(200)),
				exit = scaleOut(tween(200)),
				modifier = Modifier
					.padding(end = 16.dp)
			) {
				Button(
					shape = MaterialTheme.shapes.small,
					onClick = {
						buttonDesktopModeVisible = false
					}
				) {
					Text("Desktop Mode")
				}
			}
			
			AnimatedVisibility(
				visible = isFabExpanded,
				enter = scaleIn(tween(200)),
				exit = scaleOut(tween(200))
			) {
				FloatingActionButton(
					onClick = {
						onDesktopMode(!desktopModeEnable)
						isFabExpanded = false
					}
				) {
					Icon(
						imageVector = Icons.Rounded.DesktopWindows,
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
					Text("Refresh")
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
					Text("Exit")
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
