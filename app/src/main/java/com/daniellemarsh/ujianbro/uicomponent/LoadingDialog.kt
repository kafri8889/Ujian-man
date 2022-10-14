package com.daniellemarsh.ujianbro.uicomponent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LoadingDialog(
	isPlaying: Boolean,
	onExitClicked: () -> Unit
) {
	
	val lottieComposition by rememberLottieComposition(
		spec = LottieCompositionSpec.Asset("four_circle_spin_loading.json")
	)
	
	UjianBroPopup(onDismissRequest = {}) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.align(Alignment.Center)
		) {
			LottieAnimation(
				composition = lottieComposition,
				iterations = Int.MAX_VALUE,
				isPlaying = isPlaying,
				modifier = Modifier
					.fillMaxWidth()
					.aspectRatio(1f)
			)
			
			FilledTonalButton(onClick = onExitClicked) {
				Text("Keluar")
			}
		}
	}
}
