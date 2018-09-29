package com.gcrj.projectcontrol.util

import androidx.annotation.StringRes
import android.text.TextUtils
import android.widget.Toast
import com.gcrj.projectcontrol.BaseApplication

object ToastUtils {

    private var mToast: Toast? = null

    fun showToast(message: String?) {
        if (TextUtils.isEmpty(message)) {
            return
        }

        mToast?.cancel()
        mToast = Toast.makeText(BaseApplication.application, message, Toast.LENGTH_SHORT)
        mToast?.show()
    }

    fun showToast(@StringRes message: Int) {
        mToast?.cancel()
        mToast = Toast.makeText(BaseApplication.application, message, Toast.LENGTH_SHORT)
        mToast?.show()
    }

}
