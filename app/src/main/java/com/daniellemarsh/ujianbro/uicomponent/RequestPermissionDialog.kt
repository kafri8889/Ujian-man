package com.daniellemarsh.ujianbro.uicomponent

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniellemarsh.ujianbro.BuildConfig

@Composable
fun RequestPermissionDialog(
	onDismissRequest: () -> Unit,
	onOpenSettingsClicked: () -> Unit
) {
	
	UjianBroPopup(onDismissRequest = onDismissRequest) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
		) {
			Text(
				text = "Permission",
				style = MaterialTheme.typography.headlineSmall
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Text(
				text = "Aplikasi ini membutuhkan izin \"WRITE_EXTERNAL_STORAGE\" untuk menyimpan update-an aplikasi terbaru",
				style = MaterialTheme.typography.bodyMedium
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
					onClick = onOpenSettingsClicked
				) {
					Text("Buka pengaturan")
				}
			}
		}
	}
}
