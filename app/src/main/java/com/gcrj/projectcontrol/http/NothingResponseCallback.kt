package com.gcrj.projectcontrol.http

import com.gcrj.projectcontrol.bean.ResponseBean
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by zhangxin on 2017-7-26.
 */

abstract class NothingResponseCallback<T> : Callback<ResponseBean<T>> {

    override
    fun onResponse(call: Call<ResponseBean<T>>, response: Response<ResponseBean<T>>?) {
        if (call.isCanceled || !onStart()) {
            return
        }

        if (response == null) {
            onError("页面不存在")
            return
        }

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.status == 1) {
                onSuccess()
            } else {
                onError(body?.msg ?: "未知错误")
            }
        } else {
            onError("未知错误")
        }

        onAfter()
    }

    override
    fun onFailure(call: Call<ResponseBean<T>>, t: Throwable) {
        if (call.isCanceled || !onStart()) {
            return
        }

        when (t) {
            is UnknownHostException, is ConnectException -> onNoNet("网络不给力~")
            is SocketTimeoutException -> onError("请求超时")
            else -> onError(if (t.message == null) "网络异常" else t.message.toString())
        }

        onAfter()
    }

    //如果返回false则停止向下
    open fun onStart(): Boolean = true

    abstract fun onSuccess()

    abstract fun onError(message: String)

    abstract fun onNoNet(message: String)

    open fun onAfter() {}

}