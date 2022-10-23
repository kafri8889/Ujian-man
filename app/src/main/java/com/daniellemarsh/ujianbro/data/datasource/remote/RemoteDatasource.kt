package com.daniellemarsh.ujianbro.data.datasource.remote

import android.content.Context
import android.widget.Toast
import com.daniellemarsh.ujianbro.data.model.GithubELearingResponse
import com.daniellemarsh.ujianbro.data.model.GithubLatestAppVersionResponse
import com.daniellemarsh.ujianbro.data.model.GithubLatestVersionCodeResponse
import com.daniellemarsh.ujianbro.extension.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class RemoteDatasource @Inject constructor(
	private val githubClient: GithubClient,
	private val context: Context
) {
	
	fun getELearningUrl(
		onSuccess: (url: String) -> Unit,
		onFailure: () -> Unit
	) {
		githubClient.getELearningUrl().enqueue(object : Callback<GithubELearingResponse> {
			override fun onResponse(call: Call<GithubELearingResponse>, response: Response<GithubELearingResponse>) {
				if (response.isSuccessful) {
					response.body().let {
						if (it != null) onSuccess(response.body()!!.url)
						else onFailure()
					}
				} else {
//					response.message().toast(context)
					onFailure()
				}
			}
			
			override fun onFailure(call: Call<GithubELearingResponse>, t: Throwable) {
//				t.message?.toast(context)
				onFailure()
			}
		})
	}
	
	fun getLatestAppVersion(
		onSuccess: (appUrl: String) -> Unit,
		onFailure: () -> Unit
	) {
		githubClient.getLatestAppVersion().enqueue(object : Callback<GithubLatestAppVersionResponse> {
			override fun onResponse(call: Call<GithubLatestAppVersionResponse>, response: Response<GithubLatestAppVersionResponse>) {
				if (response.isSuccessful) {
					response.body().let {
						if (it != null) onSuccess(response.body()!!.app_url)
						else onFailure()
					}
				} else {
//					"av -> ${response.message()}".toast(context)
					onFailure()
				}
			}
			
			override fun onFailure(call: Call<GithubLatestAppVersionResponse>, t: Throwable) {
//				"av -> ${t.message}".toast(context)
				onFailure()
			}
		})
	}
	
	fun getLatestAppVersionCode(
		onSuccess: (versionCode: Int) -> Unit,
		onFailure: () -> Unit
	) {
		githubClient.getLatestAppVersionCode().enqueue(object : Callback<GithubLatestVersionCodeResponse> {
			override fun onResponse(call: Call<GithubLatestVersionCodeResponse>, response: Response<GithubLatestVersionCodeResponse>) {
				if (response.isSuccessful) {
					response.body().let {
						if (it != null) onSuccess(response.body()!!.version_code)
						else onFailure()
					}
				} else {
//					"vc -> ${response.message()}".toast(context)
					onFailure()
				}
			}
			
			override fun onFailure(call: Call<GithubLatestVersionCodeResponse>, t: Throwable) {
//				"vc -> ${t.message}".toast(context)
				onFailure()
			}
		})
	}
	
}