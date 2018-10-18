package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.view.View
import com.gcrj.projectcontrol.BaseApplication
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.util.AppManager
import com.gcrj.projectcontrol.util.startActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tv_modify_password.setOnClickListener(this)
        btn_logout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            tv_modify_password -> startActivity<ModifyPasswordActivity>()
            btn_logout -> {
                AppManager.get().finishAllActivity()
                BaseApplication.USER_INFO = null
                startActivity<LoginActivity>()
            }
        }
    }

}
