package com.daniellemarsh.ujianbro.uicomponent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExitDialog(
	correctPassword: Int,
	onDismissRequest: () -> Unit,
	onExit: () -> Unit
) {
	
	var exitPasswordUserInput by remember { mutableStateOf("") }
	
	UjianBroPopup(onDismissRequest = onDismissRequest) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
		) {
			Text(
				text = "Keluar",
				style = MaterialTheme.typography.headlineSmall
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			OutlinedTextField(
				value = exitPasswordUserInput,
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Number
				),
				onValueChange = { s ->
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
					onClick = onExit,
					enabled = correctPassword == (exitPasswordUserInput.toIntOrNull() ?: 0)
				) {
					Text("Keluar")
				}
			}
		}
	}
}
