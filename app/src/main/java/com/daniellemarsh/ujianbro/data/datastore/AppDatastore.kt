package com.daniellemarsh.ujianbro.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject

class AppDatastore @Inject constructor(private val context: Context) {
	
	
	companion object {
		val Context.datastore: DataStore<Preferences> by preferencesDataStore("app_datastore")
	}
	
}