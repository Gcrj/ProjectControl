package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.os.Handler
import com.gcrj.projectcontrol.BaseApplication
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.util.startActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
//        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            if (BaseApplication.USER_INFO == null) {
                startActivity<LoginActivity>()
            } else {
                startActivity<MainActivity>()
            }

            finish()
        }, 2000)
    }
}
