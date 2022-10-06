package com.daniellemarsh.ujianbro.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniellemarsh.ujianbro.data.datastore.AppDatastore
import com.daniellemarsh.ujianbro.extension.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HomeViewModel @Inject constructor(
	@ApplicationContext private val ctx: Context
): ViewModel() {
	
	private var listener: HomeListener? = null
	
	private val _requestedUrl = MutableStateFlow("")
	val requestedUrl: StateFlow<String> = _requestedUrl
	
	init {
		viewModelScope.launch(Dispatchers.IO) {
			val reqUrl = URL("https://kafri8889.github.io/exambroweburl.txt").readText()
			
			_requestedUrl.emit(reqUrl)
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
	
	fun exit() {
		listener?.exit()
	}
	
	fun alert() {
		listener?.alert()
	}
	
}