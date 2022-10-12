package com.daniellemarsh.ujianbro.uicomponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniellemarsh.ujianbro.BuildConfig

@Composable
fun BluetoothEnabledDialog() {

	UjianBroPopup(onDismissRequest = {}) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
		) {
			Text(
				text = "Bluetooth",
				style = MaterialTheme.typography.headlineSmall
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			Text(
				text = "Bluetooth tidak boleh dinyalakan",
				style = MaterialTheme.typography.bodyMedium
			)
		}
	}
}
