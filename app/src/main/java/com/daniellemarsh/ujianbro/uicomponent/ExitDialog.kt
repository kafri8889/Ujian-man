package com.daniellemarsh.ujianbro.uicomponent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
	ExperimentalComposeUiApi::class
)
@Composable
fun ExitDialog(
	correctPassword: String,
	onDismissRequest: () -> Unit,
	onExit: () -> Unit
) {
	
	val keyboardController = LocalSoftwareKeyboardController.current
	
	var exitPasswordUserInput by remember { mutableStateOf("") }
	var isError by remember { mutableStateOf(false) }
	
	UjianBroPopup(onDismissRequest = onDismissRequest) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.clickable(
					interactionSource = MutableInteractionSource(),
					indication = null,
					onClick = {}
				)
				.imePadding()
		) {
			Text(
				text = "Keluar",
				style = MaterialTheme.typography.headlineSmall
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			AnimatedVisibility(
				visible = isError,
				enter = scaleIn(
					animationSpec = tween(250)
				),
				exit = scaleOut(
					animationSpec = tween(250)
				),
				modifier = Modifier
					.align(Alignment.CenterHorizontally)
			) {
				Text(
					text = if (exitPasswordUserInput.isBlank()) "Masukkan password" else "Password salah",
					style = MaterialTheme.typography.bodyLarge.copy(
						color = Color(0xFFF47174)
					)
				)
			}
			
			Spacer(modifier = Modifier.height(8.dp))
			
			OutlinedTextField(
				value = exitPasswordUserInput,
				isError = isError,
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Done
				),
				keyboardActions = KeyboardActions(
					onDone = {
						keyboardController?.hide()
					}
				),
				onValueChange = { s ->
					isError = false
					exitPasswordUserInput = s
				},
				label = {
					Text("Masukkan password")
				},
				supportingText = {
					Text("Silahkan ke panitia untuk keluar dari aplikasi")
				},
				modifier = Modifier
					.fillMaxWidth()
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
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
					onClick = {
						if (exitPasswordUserInput != correctPassword) {
							isError = true
						} else onExit()
					}
				) {
					Text("Keluar")
				}
			}
		}
	}
}
