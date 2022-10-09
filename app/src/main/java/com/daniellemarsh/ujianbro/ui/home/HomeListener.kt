package com.daniellemarsh.ujianbro.ui.home

import android.net.Uri

interface HomeListener {
	
	fun exit()
	
	fun alert()
	
	fun installUpdate(uri: Uri)
	
}