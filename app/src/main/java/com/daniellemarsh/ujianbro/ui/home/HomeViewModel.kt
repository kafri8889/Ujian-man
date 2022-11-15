package com.daniellemarsh.ujianbro.ui.home

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.daniellemarsh.ujianbro.BuildConfig
import com.daniellemarsh.ujianbro.common.AlertManager
import com.daniellemarsh.ujianbro.common.DownloadItem
import com.daniellemarsh.ujianbro.common.DownloadManager
import com.daniellemarsh.ujianbro.common.networking.ConnectivityManager
import com.daniellemarsh.ujianbro.data.datasource.remote.RemoteDatasource
import com.github.h0tk3y.kotlinFun.selfReference
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	@ApplicationContext private val ctx: Context,
	private val alertManager: AlertManager,
	private val downloadManager: DownloadManager,
	private val remoteDatasource: RemoteDatasource,
	private val connectivityManager: ConnectivityManager,
): ViewModel() {
	
	private var listener: HomeListener? = null
	
	private val _effect = MutableStateFlow<HomeEffect?>(null)
	val effect: StateFlow<HomeEffect?> = _effect
	
	private val _requestedUrl = MutableStateFlow("")
	val requestedUrl: StateFlow<String> = _requestedUrl
	
	private val _latestAppVersion = MutableStateFlow(BuildConfig.VERSION_CODE)
	val latestAppVersion: StateFlow<Int> = _latestAppVersion
	
	private val _latestAppUrl = MutableStateFlow("")
	val latestAppUrl: StateFlow<String> = _latestAppUrl
	
	private val _exitPassword = MutableStateFlow(310804)
	val exitPassword: StateFlow<Int> = _exitPassword
	
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
	
	private val _isTimeout = MutableStateFlow(false)
	val isTimeout: StateFlow<Boolean> = _isTimeout
	
	val currentDownload: StateFlow<DownloadItem>
		get() = downloadManager.currentDownload
	
	private val handler = Handler(Looper.getMainLooper())
	
	private val getUrlRunnable = object : Runnable {
		override fun run() {
			remoteDatasource.getELearningUrl(
				onSuccess = { url ->
					viewModelScope.launch {
						if (url == null) {
							_effect.emit(HomeEffect.NullUrl)
						}
						
						_requestedUrl.emit(url ?: "")
						_reloadWebView.emit(true)
					}
				},
				onFailure = {
					handler.post(this)
				}
			)
		}
	}
	
	private val getLatestVersionCodeRunnable = object : Runnable {
		override fun run() {
			remoteDatasource.getLatestAppVersionCode(
				onSuccess = { versionCode ->
					viewModelScope.launch {
						if (versionCode == null) {
							_effect.emit(HomeEffect.NullUrl)
						}
						
						_latestAppVersion.emit(versionCode ?: BuildConfig.VERSION_CODE)
						_isThereANewestVersion.emit(
							versionCode > BuildConfig.VERSION_CODE
						)
					}
				},
				onFailure = {
					handler.post(this)
				}
			)
		}
	}
	
	private val getLatestAppVersionRunnable = object : Runnable {
		override fun run() {
			remoteDatasource.getLatestAppVersion(
				onSuccess = { appUrl ->
					viewModelScope.launch {
						if (appUrl == null) {
							_effect.emit(HomeEffect.NullUrl)
						}
						
						_latestAppUrl.emit(appUrl ?: "")
					}
				},
				onFailure = {
					handler.post(this)
				}
			)
		}
	}
	
	private val getExitPasswordRunnable = object : Runnable {
		override fun run() {
			remoteDatasource.getExitPassword(
				onSuccess = { password ->
					viewModelScope.launch {
						if (password == null) {
							_effect.emit(HomeEffect.NullUrl)
						}
						
						_exitPassword.emit(password ?: 310804)
					}
				},
				onFailure = {
					handler.post(this)
				}
			)
		}
	}
	
	private val getTimeoutResponseRunnable: Runnable = selfReference {
		Runnable {
			Timber.i("runnig pos")
			remoteDatasource.getTimeoutResponse(
				onSuccess = { response ->
					Timber.i("runnig sukkes?: $response")
					viewModelScope.launch(Dispatchers.IO) {
						_isTimeout.emit(false)
						Timber.i("runnig respon bifor -> ${isTimeout.value}")
						delay(500)
						_isTimeout.emit(response == null)
						delay(1000)
						withContext(Dispatchers.Main) {
							handler.post(self)
						}
						Timber.i("runnig respon after -> ${isTimeout.value}")
					}
				},
				onTimeoutReset = {}
			)
		}
	}
	
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
		
		handler.post(getTimeoutResponseRunnable)
		handler.post(getUrlRunnable)
		handler.post(getLatestAppVersionRunnable)
		handler.post(getLatestVersionCodeRunnable)
		handler.post(getExitPasswordRunnable)
		
		viewModelScope.launch {
			connectivityManager.isNetworkAvailable.asFlow().collect { available ->
				_isNetworkHaveInternet.emit(available)
			}
		}
		
//		viewModelScope.launch(Dispatchers.IO) {
//			connectivityManager.isNetworkAvailable.asFlow().collect { available ->
//				_isNetworkHaveInternet.emit(available)
//
//				available.toast(ctx)
//				if (available) {
//					val reqUrl = try {
//						URL(Constant.E_LEARNING_URL).readText()
//					} catch (e: ConnectException) {
//						_effect.emit(
//							HomeEffect.NetworkException("Terjadi kesalahan pada internet")
//						); ""
//					} catch (e: Exception) {
//						_effect.emit(
//							HomeEffect.NetworkException(e.message ?: "Terjadi kesalahan")
//						); ""
//					}.also { it.toast(ctx) }
//
//					_requestedUrl.emit(reqUrl)
//					_reloadWebView.emit(true)
//				}
//			}
//		}
		
//		viewModelScope.launch(Dispatchers.IO) {
////			val latestAppVer = 7
//			val latestAppVer = try {
//				URL(Constant.LATEST_APP_VERSION_CODE_URL).readText().toInt()
//			} catch (e: Exception) { BuildConfig.VERSION_CODE }
//
//			_latestAppVersion.emit(latestAppVer)
//			_isThereANewestVersion.emit(
//				latestAppVer > BuildConfig.VERSION_CODE
//			)
//		}
	}
	
	fun setListener(mListener: HomeListener) {
		this.listener = mListener
	}
	
	fun setTimeout(timeout: Boolean) {
		viewModelScope.launch {
			_isTimeout.emit(timeout)
		}
	}
	
	fun setReloadWebView(reload: Boolean) {
		viewModelScope.launch {
			if (reload and requestedUrl.value.isBlank()) {
				handler.post(getUrlRunnable)
			} else _reloadWebView.emit(reload)
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
			
			if (latestAppUrl.value.isNotBlank()) {
				withContext(Dispatchers.Main) {
					downloadManager.download(
						url = latestAppUrl.value,
						fileName = latestAppUrl.value.substringAfterLast('/')
					)
				}
			} else {
				_effect.emit(HomeEffect.BlankLatestAppVersionUrl)
				_isDownloading.emit(false)
			}
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
		alertManager.allowAlert(TAG, true)
	}
	
	fun disallowAlert() {
		alertManager.allowAlert = false
	}
	
	fun enableSecurity() {
		listener?.enableSecurity()
	}
	
	fun disableSecurity() {
		listener?.disableSecurity()
	}
	
	fun exit() {
		listener?.exit()
	}
	
	fun alert() {
		alertManager.start()
	}
	
	companion object {
		const val TAG = "HomeViewModel"
	}
	
}