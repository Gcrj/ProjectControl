package com.gcrj.projectcontrol.activity

import android.os.Bundle
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
