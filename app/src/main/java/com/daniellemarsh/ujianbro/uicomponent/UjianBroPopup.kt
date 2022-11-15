package com.daniellemarsh.ujianbro.uicomponent

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UjianBroPopup(
	onDismissRequest: () -> Unit,
	content: @Composable BoxScope.() -> Unit
) {
	
	val surfaceColor by animateColorAsState(
		targetValue = MaterialTheme.colorScheme.surface,
		animationSpec = tween(400)
	)
	
	val scrimColor by animateColorAsState(
		targetValue = Color.Black.copy(alpha = 0.6f),
		animationSpec = tween(400)
	)
	
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier
			.fillMaxSize()
			.background(scrimColor)
			.clickable(
				enabled = true,
				interactionSource = MutableInteractionSource(),
				indication = null,
				onClick = onDismissRequest
			)
			.padding(24.dp)
	) {
		Card(
			shape = MaterialTheme.shapes.extraLarge,
			colors = CardDefaults.cardColors(
				containerColor = surfaceColor
			)
		) {
			Spacer(modifier = Modifier.height(24.dp))
			
			Box(
				content = content,
				modifier = Modifier
					.padding(horizontal = 16.dp)
			)
			
			Spacer(modifier = Modifier.height(24.dp))
		}
	}
}
