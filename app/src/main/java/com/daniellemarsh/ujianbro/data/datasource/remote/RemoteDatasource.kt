package com.daniellemarsh.ujianbro.data.datasource.remote

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.daniellemarsh.ujianbro.BuildConfig
import com.daniellemarsh.ujianbro.data.Constant
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class RemoteDatasource @Inject constructor(
	private val githubClient: GithubClient,
	private val context: Context
) {
	
	private val queue = Volley.newRequestQueue(context)
	
	fun getELearningUrl(
		onSuccess: (url: String) -> Unit,
		onFailure: () -> Unit
	) {
		val req = JsonObjectRequest(
			Constant.E_LEARNING_URL,
			{
				onSuccess(it.get("url").toString())
			}
		) {
			onFailure()
		}
		
		queue.add(req)
		queue.start()
		
//		githubClient.getELearningUrl().enqueue(object : Callback<GithubELearingResponse> {
//			override fun onResponse(call: Call<GithubELearingResponse>, response: Response<GithubELearingResponse>) {
//				if (response.isSuccessful) {
//					if (response.body() != null) {
//						onSuccess(response.body()?.url ?: "")
//					} else onFailure()
//				} else {
////					response.message().toast(context)
//					onFailure()
//				}
//			}
//
//			override fun onFailure(call: Call<GithubELearingResponse>, t: Throwable) {
////				t.message?.toast(context)
//				onFailure()
//			}
//		})
	}
	
	fun getLatestAppVersion(
		onSuccess: (appUrl: String) -> Unit,
		onFailure: () -> Unit
	) {
		val req = JsonObjectRequest(
			Constant.LATEST_VERSION_APP_URL,
			{
				onSuccess(it.get("app_url").toString())
			}
		) {
			onFailure()
		}
		
		queue.add(req)
		queue.start()
//		githubClient.getLatestAppVersion().enqueue(object : Callback<GithubLatestAppVersionResponse> {
//			override fun onResponse(call: Call<GithubLatestAppVersionResponse>, response: Response<GithubLatestAppVersionResponse>) {
//				if (response.isSuccessful) {
//					if (response.body() != null) {
//						onSuccess(response.body()?.app_url ?: "")
//					} else onFailure()
//				} else {
////					"av -> ${response.message()}".toast(context)
//					onFailure()
//				}
//			}
//
//			override fun onFailure(call: Call<GithubLatestAppVersionResponse>, t: Throwable) {
////				"av -> ${t.message}".toast(context)
//				onFailure()
//			}
//		})
	}
	
	fun getLatestAppVersionCode(
		onSuccess: (versionCode: Int) -> Unit,
		onFailure: () -> Unit
	) {
		val req = JsonObjectRequest(
			Constant.LATEST_APP_VERSION_CODE_URL,
			{
				onSuccess(
					try {
						it.get("version_code").toString().toInt()
					} catch (e: Exception) { BuildConfig.VERSION_CODE }
				)
			}
		) {
			onFailure()
		}
		
		queue.add(req)
		queue.start()
//		githubClient.getLatestAppVersionCode().enqueue(object : Callback<GithubLatestVersionCodeResponse> {
//			override fun onResponse(call: Call<GithubLatestVersionCodeResponse>, response: Response<GithubLatestVersionCodeResponse>) {
//				if (response.isSuccessful) {
//					if (response.body() != null) {
//						onSuccess(response.body()?.version_code ?: BuildConfig.VERSION_CODE)
//					} else onFailure()
//				} else {
////					"vc -> ${response.message()}".toast(context)
//					onFailure()
//				}
//			}
//
//			override fun onFailure(call: Call<GithubLatestVersionCodeResponse>, t: Throwable) {
////				"vc -> ${t.message}".toast(context)
//				onFailure()
//			}
//		})
	}
	
	fun getExitPassword(
		onSuccess: (pass: String) -> Unit,
		onFailure: () -> Unit
	) {
		val req = JsonObjectRequest(
			Constant.EXIT_PASSWORD_URL,
			{
				onSuccess(
					try {
						it.get("exit_password").toString()
					} catch (e: Exception) { "310804" }
				)
			}
		) {
			onFailure()
		}
		
		queue.add(req)
		queue.start()
	}
	
	fun getTimeoutResponse(
		onSuccess: (response: Int?) -> Unit,
		onTimeoutReset: () -> Unit
	) {
		val successListener = Response.Listener<JSONObject> {
			onSuccess(
				try {
					it.get("response").toString().toInt()
				} catch (e: Exception) { null }
			)
		}
		
		val errorListener = Response.ErrorListener {
//			it.message.toast(context)
			onTimeoutReset()
			if (it.networkResponse == null) {
				if (it.javaClass == TimeoutError::class.java) {
					onSuccess(null)
				}
			}
		}
		
		val req = JsonObjectRequest(
			Constant.TO_CHECK_TIMEOUT_URL,
			successListener,
			errorListener
		).apply {
			setShouldCache(false)
			retryPolicy = DefaultRetryPolicy(3800, 0, 0f)
		}
		
		queue.cancelAll { it.url == req.url }
		queue.add(req)
		Timber.i("runnig timeout add to qyuyu")
	}
	
}