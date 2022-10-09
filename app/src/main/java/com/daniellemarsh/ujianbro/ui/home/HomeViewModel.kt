package com.daniellemarsh.ujianbro.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.daniellemarsh.ujianbro.common.networking.ConnectivityManager
import com.daniellemarsh.ujianbro.data.Constant
import com.daniellemarsh.ujianbro.data.datastore.AppDatastore
import com.daniellemarsh.ujianbro.extension.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.URL
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HomeViewModel @Inject constructor(
	@ApplicationContext private val ctx: Context,
	private val connectivityManager: ConnectivityManager
): ViewModel() {
	
	private var listener: HomeListener? = null
	
	private val _effect = MutableStateFlow<HomeEffect?>(null)
	val effect: StateFlow<HomeEffect?> = _effect
	
	private val _requestedUrl = MutableStateFlow("")
	val requestedUrl: StateFlow<String> = _requestedUrl
	
	private val _reloadWebView = MutableStateFlow(false)
	val reloadWebView: StateFlow<Boolean> = _reloadWebView
	
	private val _isNetworkHaveInternet = MutableStateFlow(false)
	val isNetworkHaveInternet: StateFlow<Boolean> = _isNetworkHaveInternet
	
	init {
		viewModelScope.launch(Dispatchers.IO) {
			connectivityManager.isNetworkAvailable.asFlow().collect { available ->
				_isNetworkHaveInternet.emit(available)
				
				if (available) {
					val reqUrl = try {
						URL(Constant.E_LEARNING_URL).readText()
					} catch (e: ConnectException) {
						_effect.emit(
							HomeEffect.NetworkException("Terjadi kesalahan pada internet")
						); ""
					} catch (e: Exception) {
						_effect.emit(
							HomeEffect.NetworkException(e.message ?: "Terjadi kesalahan")
						); ""
					}

					_requestedUrl.emit(reqUrl)
					_reloadWebView.emit(true)
				}
			}
		}
	}
	
	fun setListener(mListener: HomeListener) {
		this.listener = mListener
	}
	
	fun setRequestedUrl(s: String) {
		viewModelScope.launch {
			_requestedUrl.emit(s)
		}
	}
	
	fun setReloadWebView(reload: Boolean) {
		viewModelScope.launch(Dispatchers.IO) {
			_reloadWebView.emit(reload)
		}
	}
	
	fun resetEffect() {
		viewModelScope.launch {
			_effect.emit(null)
		}
	}
	
	fun exit() {
		listener?.exit()
	}
	
	fun alert() {
		listener?.alert()
	}
	
}