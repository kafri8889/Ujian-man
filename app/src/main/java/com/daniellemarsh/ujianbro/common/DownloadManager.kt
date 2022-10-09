package com.daniellemarsh.ujianbro.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils.replace
import android.webkit.DownloadListener
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.daniellemarsh.ujianbro.BuildConfig
import com.daniellemarsh.ujianbro.extension.toast
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.random.Random

data class DownloadItem(
	val id: Int,
	val progress: Int,
	val url: String,
	val fileName: String,
	val uri: Uri
) {
	companion object {
		val NULL = DownloadItem(
			id = -1,
			progress = 0,
			url = "",
			fileName = "",
			uri = Uri.EMPTY
		)
	}
}

class DownloadManager @Inject constructor(
	private val context: Context
) {
	
	private var fetch: Fetch? = null
	private var listener: DownloadListener? = null
	
	private val _currentDownload = MutableStateFlow(DownloadItem.NULL)
	val currentDownload: StateFlow<DownloadItem> = _currentDownload
	
	init {
		val config = FetchConfiguration.Builder(context)
			.setDownloadConcurrentLimit(1)
			.build()
		
		fetch = Fetch.Impl.getInstance(config)
		fetch!!.addListener(object : FetchListener {
			override fun onAdded(download: Download) {}
			
			override fun onCancelled(download: Download) {}
			
			override fun onCompleted(download: Download) {
				listener?.onSuccess(download.id)
			}
			
			override fun onDeleted(download: Download) {}
			
			override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}
			
			override fun onError(download: Download, error: Error, throwable: Throwable?) {
				listener?.onError(download.id, throwable?.message ?: "")
			}
			
			override fun onPaused(download: Download) {}
			
			override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
				if (download.id == currentDownload.value.id) {
					CoroutineScope(Dispatchers.IO).launch {
						_currentDownload.emit(
							currentDownload.value.copy(
								progress = download.progress
							)
						)
					}
				}
			}
			
			override fun onQueued(download: Download, waitingOnNetwork: Boolean) {}
			
			override fun onRemoved(download: Download) {}
			
			override fun onResumed(download: Download) {}
			
			override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {}
			
			override fun onWaitingNetwork(download: Download) {}
		})
	}
	
	fun download(url: String, fileName: String) {
//		val mFileName = "16app-release.apk"
		val mFileName = Random(System.currentTimeMillis()).nextInt(0, 100000).toString() + "-$fileName"
			.replace(" ", "")
			.replace("\n", "")
// 		val mFileName = fileName
//			.replace(" ", "")
//			.replace("\n", "")
		
		val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
		
		if (!downloadDir.exists()) {
			downloadDir.mkdirs()
		}
		
		val examBroDir = File(downloadDir, "UjianBro")
		
		if (!examBroDir.exists()) {
			examBroDir.mkdirs()
		}
//		fileName.toast(context, Toast.LENGTH_LONG)
//		return
//		downloadDir.absolutePath.toast(context, Toast.LENGTH_LONG)
		val targetFileDir = File(
			examBroDir,
			mFileName
		)
		
//		targetFileDir.absolutePath.toast(context, Toast.LENGTH_LONG)
		
		if (!targetFileDir.exists()) {
			targetFileDir.createNewFile()
		} else {
			targetFileDir.delete()
			targetFileDir.createNewFile()
		}
		
		val targetUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			FileProvider.getUriForFile(
				context,
				"${BuildConfig.APPLICATION_ID}.provider",
				targetFileDir
			)
		} else {
			targetFileDir.toUri()
		}
		
		Timber.i("targetUri pathhh: ${targetUri.path}, $targetUri")
		
		val req = Request(
			url,
			targetUri
		).apply {
			priority = Priority.HIGH
			networkType = NetworkType.ALL
		}
		
		CoroutineScope(Dispatchers.Main).launch {
			_currentDownload.emit(
				DownloadItem(
					id = req.id,
					progress = 0,
					url = url,
					fileName = mFileName,
					uri = targetUri
				)
			)
		}
		
		fetch?.cancelAll()
		fetch?.enqueue(req, { request ->
		
		}, { error ->
			// Error
			error.throwable?.message?.let {
				it.toast(context)
			}
		})
	}
	
	fun setListener(l: DownloadListener) {
		listener = l
	}
	
	interface DownloadListener {
		
		fun onError(id: Int, message: String)
		
		fun onSuccess(id: Int)
	}
	
}