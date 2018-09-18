package com.gcrj.projectcontrol.http

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.gcrj.projectcontrol.BaseApplication
import com.gcrj.projectcontrol.BuildConfig
import com.gcrj.projectcontrol.util.Constant
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitManager {

    companion object {
        val apiService
            get() = SingletonHolder.API_SERVICE
    }

    private object SingletonHolder {
        var OK_HTTP_CLIENT: OkHttpClient
        var API_SERVICE: ApiService

        init {
            val cacheSize = 10 * 1024 * 1024 // 10 MB
            val cache = Cache(BaseApplication.application.cacheDir, cacheSize.toLong())
            val builder = OkHttpClient.Builder().cache(cache)
            if (BuildConfig.DEBUG) {
                builder.addNetworkInterceptor(StethoInterceptor())
            }

            val privateKeyInterceptor = Interceptor {
                it.proceed(it.request().newBuilder().addHeader("token", BaseApplication.USER_INFO?.token
                        ?: "").build())
            }
            builder.addInterceptor(privateKeyInterceptor)

            OK_HTTP_CLIENT = builder.build()
            API_SERVICE = Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .callFactory(OK_HTTP_CLIENT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ApiService::class.java)
        }
    }

}