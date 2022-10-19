package com.daniellemarsh.ujianbro.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.daniellemarsh.ujianbro.BuildConfig
import com.daniellemarsh.ujianbro.common.AlertManager
import com.daniellemarsh.ujianbro.common.DownloadItem
import com.daniellemarsh.ujianbro.common.DownloadManager
import com.daniellemarsh.ujianbro.common.networking.ConnectivityManager
import com.daniellemarsh.ujianbro.data.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.ConnectException
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
//	@ApplicationContext private val ctx: Context,
	private val alertManager: AlertManager,
	private val downloadManager: DownloadManager,
	private val connectivityManager: ConnectivityManager
): ViewModel() {
	
	private var listener: HomeListener? = null
	
	private val _effect = MutableStateFlow<HomeEffect?>(null)
	val effect: StateFlow<HomeEffect?> = _effect
	
	private val _requestedUrl = MutableStateFlow("")
	val requestedUrl: StateFlow<String> = _requestedUrl
	
	private val _latestAppVersion = MutableStateFlow(BuildConfig.VERSION_CODE)
	val latestAppVersion: StateFlow<Int> = _latestAppVersion
	
	private val _reloadWebView = MutableStateFlow(false)
	val reloadWebView: StateFlow<Boolean> = _reloadWebView
	
	private val _isDownloading = MutableStateFlow(false)
	val isDownloading: StateFlow<Boolean> = _isDownloading
	
	private val _isNetworkHaveInternet = MutableStateFlow(false)
	val isNetworkHaveInternet: StateFlow<Boolean> = _isNetworkHaveInternet
	
	private val _isThereANewestVersion = MutableStateFlow(false)
	val isThereANewestVersion: StateFlow<Boolean> = _isThereANewestVersion
	
	private val _isBluetoothEnabled = MutableStateFlow(false)
	val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled
	
	val currentDownload: StateFlow<DownloadItem>
		get() = downloadManager.currentDownload
	
	init {
		downloadManager.setListener(object : DownloadManager.DownloadListener {
			override fun onError(id: Int, message: String) {
				if (id == currentDownload.value.id) {
					Timber.e("download error: $message")
					
					viewModelScope.launch {
						_isDownloading.emit(false)
					}
				}
			}
			
			override fun onSuccess(id: Int) {
				Timber.i("download success")
				viewModelScope.launch(Dispatchers.IO) {
					_isDownloading.emit(false)
					_isThereANewestVersion.emit(false)
					
					listener?.installUpdate(currentDownload.value.uri)
//					"Download Selesai".toast(ctx)
				}
			}
		})
		
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
		
		viewModelScope.launch(Dispatchers.IO) {
//			val latestAppVer = 7
			val latestAppVer = try {
				URL(Constant.LATEST_APP_VERSION_CODE_URL).readText().toInt()
			} catch (e: Exception) { BuildConfig.VERSION_CODE }
			
			_latestAppVersion.emit(latestAppVer)
			_isThereANewestVersion.emit(
				latestAppVer > BuildConfig.VERSION_CODE
			)
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
	
	fun setIsThereANewestVersion(show: Boolean) {
		viewModelScope.launch {
			_isThereANewestVersion.emit(show)
		}
	}
	
	fun downloadLatestVersion() {
		viewModelScope.launch(Dispatchers.IO) {
			_isDownloading.emit(true)
			
//			val latestAppUrl = "https://github.com/kafri8889/kafri8889.github.io/raw/main/data/ujianbro/app-release.apk"
			val latestAppUrl = try {
				URL(Constant.LATEST_VERSION_APP_URL).readText()
			} catch (e: Exception) { "" }
//			latestAppUrl.toast(ctx)
			if (latestAppUrl.isNotBlank()) {
				withContext(Dispatchers.Main) {
					downloadManager.download(
						url = latestAppUrl,
						fileName = latestAppUrl.substringAfterLast('/')
					)
				}
			} else _isDownloading.emit(false)
		}
	}
	
	fun setBluetoothEnabled(isEnabled: Boolean) {
		viewModelScope.launch {
			_isBluetoothEnabled.emit(isEnabled)
		}
	}
	
	fun resetEffect() {
		viewModelScope.launch {
			_effect.emit(null)
		}
	}
	
	fun allowAlert() {
		alertManager.allowAlert = true
	}
	
	fun disallowAlert() {
		alertManager.allowAlert = false
	}
	
	fun exit() {
		listener?.exit()
	}
	
	fun alert() {
		alertManager.start()
	}
	
}