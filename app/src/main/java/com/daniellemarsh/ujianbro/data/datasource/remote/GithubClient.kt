package com.daniellemarsh.ujianbro.data.datasource.remote

import com.daniellemarsh.ujianbro.data.model.GithubELearingResponse
import com.daniellemarsh.ujianbro.data.model.GithubLatestAppVersionResponse
import com.daniellemarsh.ujianbro.data.model.GithubLatestVersionCodeResponse
import retrofit2.Call
import retrofit2.http.GET

interface GithubClient {
	
	@GET("/data/ujianbro/exambro_web_url.txt")
	fun getELearningUrl(): Call<GithubELearingResponse>
	
	@GET("/data/ujianbro/current_app_version_code.txt")
	fun getLatestAppVersionCode(): Call<GithubLatestVersionCodeResponse>
	
	@GET("/data/ujianbro/latest_version_app_url.txt")
	fun getLatestAppVersion(): Call<GithubLatestAppVersionResponse>
	
}