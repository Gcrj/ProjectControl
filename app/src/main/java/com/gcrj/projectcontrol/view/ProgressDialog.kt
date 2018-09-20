package com.gcrj.projectcontrol.view

import android.app.ProgressDialog
import android.content.Context

/**
 * Created by zhangxin on 2018/9/19.
 */
class ProgressDialog(context: Context?) : ProgressDialog(context) {

    init {
        setMessage("正在提交至服务器")
        setCancelable(false)
    }

}