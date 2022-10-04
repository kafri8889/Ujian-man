package com.daniellemarsh.ujianbro.ui.home

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsControllerCompat
import com.daniellemarsh.ujianbro.MainActivity
import com.daniellemarsh.ujianbro.R
import com.daniellemarsh.ujianbro.common.DelayManager
import com.daniellemarsh.ujianbro.extension.toast
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen(
	viewModel: HomeViewModel
) {
	
	val context = LocalContext.current

	val scope = rememberCoroutineScope()
	val systemUiController = rememberSystemUiController()
	
	val delayManager = remember { DelayManager() }
	
	val requestedUrl by viewModel.requestedUrl.collectAsState()
	
	var isFabExpanded by remember { mutableStateOf(false) }
	var buttonExitVisible by remember { mutableStateOf(true) }
	var buttonRefreshVisible by remember { mutableStateOf(true) }
	
	val iconRotationDegree by animateFloatAsState(
		targetValue = if (isFabExpanded) 360f else 0f,
		animationSpec = tween(200)
	)

	SideEffect {
		systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
		systemUiController.isNavigationBarVisible = false
	}
	
	Box(
		modifier = Modifier
			.fillMaxSize()
	) {
		Column(
			horizontalAlignment = Alignment.End,
			modifier = Modifier
				.padding(16.dp)
				.zIndex(1f)
				.align(Alignment.BottomEnd)
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
						colors = ButtonDefaults.buttonColors(
							containerColor = Color(0xFF07c9ae)
						),
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
						
						}
					) {
						Icon(
							painter = painterResource(id = R.drawable.ic_autorenew),
							contentDescription = null,
							tint = Color.White,
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
						colors = ButtonDefaults.buttonColors(
							containerColor = Color(0xFF07c9ae)
						),
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
						onClick = {
							(context as MainActivity).finishAffinity()
						}
					) {
						Icon(
							imageVector = Icons.Rounded.Close,
							contentDescription = null,
							tint = Color.White
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
					tint = Color.White,
					modifier = Modifier
						.rotate(iconRotationDegree)
				)
			}
		}
		
		Column(
			modifier = Modifier
				.fillMaxSize()
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
								
								webViewClient = WebViewClient()
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
							it.loadUrl(requestedUrl)
						},
						modifier = Modifier
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

}
