package com.daniellemarsh.ujianbro.common.networking

import android.content.Context
import com.daniellemarsh.ujianbro.common.networking.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule {
	
	@Provides
	@Singleton
	fun provideConnectivityManager(
		@ApplicationContext context: Context
	): ConnectivityManager = ConnectivityManager(context)
	
}