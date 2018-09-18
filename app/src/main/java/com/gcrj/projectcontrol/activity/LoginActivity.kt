package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.gcrj.projectcontrol.BaseApplication
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.bean.LoginBean
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.util.Tool
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus

class LoginActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btn_login.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_login -> {
                val username = et_username.text.toString()
                val password = et_password.text.toString()
                if (username.trim() == "" || password.trim() == "") {
                    ToastUtils.showToast("请输入正确的用户名和密码")
                    return
                }

                RetrofitManager.apiService.login(username, Tool.getMD5Str(password)
                        ?: "").enqueue(callback)
            }
        }
    }

    private val callback by lazy {
        object : ResponseCallback<LoginBean>() {

            override fun onStart() = !isDestroyed

            override fun onSuccess(data: LoginBean) {
                BaseApplication.USER_INFO = data
                ToastUtils.showToast("登录成功")
                EventBus.getDefault().post(data)
                finish()
            }

            override fun onError(message: String) {
                ToastUtils.showToast(message)
            }

            override fun onNoNet(message: String) {
                ToastUtils.showToast(message)
            }
        }
    }

}
