package com.daniellemarsh.ujianbro.receiver

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothReceiver(
	private val onDisabled: () -> Unit = {},
	private val onEnabled: () -> Unit = {}
): BroadcastReceiver() {
	
	override fun onReceive(context: Context, intent: Intent?) {
		if (intent == null) return
		
		when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
			BluetoothAdapter.STATE_OFF -> {
				onDisabled()
			}
			BluetoothAdapter.STATE_ON -> {
				onEnabled()
			}
		}
	}
}