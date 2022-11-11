package com.daniellemarsh.ujianbro.ui.home

sealed interface HomeEffect {
	data class NetworkException(val message: String): HomeEffect
	
	object NullUrl: HomeEffect
	object BlankLatestAppVersionUrl: HomeEffect
	object DownloadLatestAppComplete: HomeEffect
}