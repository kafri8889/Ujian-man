package com.daniellemarsh.ujianbro.data.datasource

import android.content.Context
import com.daniellemarsh.ujianbro.data.datasource.remote.GithubClient
import com.daniellemarsh.ujianbro.data.datasource.remote.GithubServer
import com.daniellemarsh.ujianbro.data.datasource.remote.RemoteDatasource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatasourceModule {
	
	@Provides
	@Singleton
	fun provideRetrofit(): Retrofit = Retrofit.Builder()
		.baseUrl(GithubServer.BASE_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.build()
	
	@Provides
	@Singleton
	fun provideGithubClient(
		retrofit: Retrofit
	): GithubClient = retrofit.create(GithubClient::class.java)
	
	@Provides
	@Singleton
	fun provideRemoteDatasource(
		@ApplicationContext context: Context,
		githubClient: GithubClient
	): RemoteDatasource = RemoteDatasource(
		context = context,
		githubClient = githubClient
	)
	
}