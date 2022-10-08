package com.daniellemarsh.ujianbro.uicomponent

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LoadingDialog(
	isPlaying: Boolean
) {
	
	val lottieComposition by rememberLottieComposition(
		spec = LottieCompositionSpec.Asset("four_circle_spin_loading.json")
	)
	
	UjianBroPopup(onDismissRequest = {}) {
		LottieAnimation(
			composition = lottieComposition,
			iterations = Int.MAX_VALUE,
			isPlaying = isPlaying,
			modifier = Modifier
				.fillMaxWidth()
				.aspectRatio(1f)
				.align(Alignment.Center)
		)
	}
}
