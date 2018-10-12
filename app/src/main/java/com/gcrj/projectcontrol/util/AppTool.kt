package com.gcrj.projectcontrol.util

import com.gcrj.projectcontrol.bean.CheckUpdateBean
import com.gcrj.projectcontrol.bean.ResponseBean
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import retrofit2.Call

/**
 * Created by zhangxin on 2018/6/8.
 */
object AppTool {

    fun checkUpdate(callback: ((bean: CheckUpdateBean?, msg: String?) -> Unit)): Call<ResponseBean<CheckUpdateBean>> {
        val call = RetrofitManager.apiService.checkUpdate()
        try {
            return call
        } finally {
            call.enqueue(object : ResponseCallback<CheckUpdateBean>() {

                override fun onSuccess(data: CheckUpdateBean) {
                    callback.invoke(data, null)
                }

                override fun onError(message: String) {
                    callback.invoke(null, message)
                }

                override fun onNoNet(message: String) {
                    callback.invoke(null, message)
                }

            })
        }
    }

}