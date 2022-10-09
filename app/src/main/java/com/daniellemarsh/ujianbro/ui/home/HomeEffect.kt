package com.daniellemarsh.ujianbro.ui.home

sealed interface HomeEffect {
	data class NetworkException(val message: String): HomeEffect
	
	object DownloadLatestAppComplete: HomeEffect
}