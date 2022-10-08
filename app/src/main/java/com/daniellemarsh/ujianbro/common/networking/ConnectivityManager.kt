package com.daniellemarsh.ujianbro.common.networking

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import javax.inject.Inject

class ConnectivityManager @Inject constructor(
	context: Context
) {
	
	private val connectionLiveData = NetworkConnectivity(context)
	
	private val _isNetworkAvailable = MutableLiveData(false)
	val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable
	
	fun registerConnectionObserver(lifecycleOwner: LifecycleOwner){
		connectionLiveData.observe(lifecycleOwner) { isConnected ->
			Timber.i("is connected: $isConnected")
			_isNetworkAvailable.value = isConnected
		}
	}
	
	fun unregisterConnectionObserver(lifecycleOwner: LifecycleOwner){
		connectionLiveData.removeObservers(lifecycleOwner)
	}

}