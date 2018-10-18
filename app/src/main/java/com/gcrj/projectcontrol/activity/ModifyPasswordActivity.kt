package com.gcrj.projectcontrol.activity

import android.os.Bundle
import com.gcrj.projectcontrol.BaseApplication
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.http.NothingResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.AppManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.util.Tool
import com.gcrj.projectcontrol.util.startActivity
import com.gcrj.projectcontrol.view.ProgressDialog
import kotlinx.android.synthetic.main.activity_modify_password.*

class ModifyPasswordActivity : BaseActivity() {

    private val dialog by lazy {
        ProgressDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_password)
        btn.setOnClickListener {
            val password = et_password.text.toString().trim()
            if (password == "") {
                ToastUtils.showToast(tiet_password.hint.toString())
                return@setOnClickListener
            }

            val md5Password = Tool.getMD5Str(password)
            if (md5Password == null) {
                ToastUtils.showToast("md5有误")
                return@setOnClickListener
            }

            dialog.show()
            RetrofitManager.apiService.modifyPassword(md5Password).enqueue(nothingCallback)
        }
    }

    private val nothingCallback by lazy {
        object : NothingResponseCallback<Nothing>() {

            override fun onStart() = !isDestroyed

            override fun onSuccess() {
                ToastUtils.showToast("修改成功")
                AppManager.get().finishAllActivity()
                BaseApplication.USER_INFO = null
                startActivity<LoginActivity>()
            }

            override fun onError(message: String) {
                ToastUtils.showToast(message)
            }

            override fun onNoNet(message: String) {
                ToastUtils.showToast(message)
            }

            override fun onAfter() {
                dialog.dismiss()
            }

        }
    }

}
