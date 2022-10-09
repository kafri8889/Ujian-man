package com.daniellemarsh.ujianbro.uicomponent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniellemarsh.ujianbro.BuildConfig

@Composable
fun NewVersionDialog(
	isDownloading: Boolean,
	downloadProgress: Int,
	latestVersionCode: Int,
	onUpdateClicked: () -> Unit,
	onDismissRequest: () -> Unit
) {
	
	val currentProgress = remember(downloadProgress) {
		try {
			downloadProgress.toFloat() / 100f
		} catch (e: ArithmeticException) { 0f }
	}
	
	val downloadProgressFraction by animateFloatAsState(
		targetValue = if (isDownloading) 1f else 0f,
		animationSpec = tween(1000, 100)
	)

	UjianBroPopup(onDismissRequest = onDismissRequest) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
		) {
			Text(
				text = "Versi baru tersedia",
				style = MaterialTheme.typography.headlineSmall
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			Text(
				text = "Perbarui versi dari ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) ke ($latestVersionCode)\n" +
						"Lokasi unduhan \"/Download/UjianBro/{id}-{nama}.apk\"",
				style = MaterialTheme.typography.bodyMedium
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			AnimatedVisibility(
				visible = isDownloading,
				enter = fadeIn(
					animationSpec = tween(500)
				),
				exit = fadeOut(
					animationSpec = tween(500)
				)
			) {
				Column {
					Row(
						modifier = Modifier
							.fillMaxWidth()
					) {
						Text("Downloading...")
						
						Spacer(modifier = Modifier.weight(1f))
						
						Text("$downloadProgress %")
					}
					
					Spacer(modifier = Modifier.height(8.dp))
					
					LinearProgressIndicator(
						progress = currentProgress,
						modifier = Modifier
							.fillMaxWidth(downloadProgressFraction)
					)
					
					Spacer(modifier = Modifier.height(16.dp))
				}
			}
			
			Row(
				horizontalArrangement = Arrangement.End,
				modifier = Modifier
					.fillMaxWidth()
			) {
				TextButton(
					onClick = onDismissRequest
				) {
					Text("Tutup")
				}
				
				Spacer(modifier = Modifier.width(8.dp))
				
				Button(
					onClick = onUpdateClicked
				) {
					Text("Update")
				}
			}
		}
	}
}
