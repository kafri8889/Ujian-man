package com.daniellemarsh.ujianbro.common

import android.content.Context
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
	
}