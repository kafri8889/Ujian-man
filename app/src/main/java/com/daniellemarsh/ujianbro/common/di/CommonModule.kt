package com.daniellemarsh.ujianbro.common.di

import android.content.Context
import com.daniellemarsh.ujianbro.common.AlertManager
import com.daniellemarsh.ujianbro.common.DownloadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {
	
	@Singleton
	@Provides
	fun provideAlertManager(
		@ApplicationContext context: Context
	): AlertManager {
		return AlertManager(context)
	}
	
	@Singleton
	@Provides
	fun provideDownloadManager(
		@ApplicationContext context: Context
	): DownloadManager {
		return DownloadManager(context)
	}
	
}