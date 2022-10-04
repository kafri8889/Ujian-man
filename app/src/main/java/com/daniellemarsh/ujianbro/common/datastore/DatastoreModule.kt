package com.daniellemarsh.ujianbro.common.datastore

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatastoreModule {
	
	@Singleton
	@Provides
	fun provideAppDatastore(
		@ApplicationContext context: Context
	): AppDatastore = AppDatastore(
		context = context
	)
	
}