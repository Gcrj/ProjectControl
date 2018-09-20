package com.gcrj.projectcontrol.fragment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.gcrj.projectcontrol.BaseApplication
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.activity.SettingActivity
import com.gcrj.projectcontrol.base.BaseFragment
import com.gcrj.projectcontrol.util.startActivity
import kotlinx.android.synthetic.main.fragment_mine.*

class MineFragment : BaseFragment(), View.OnClickListener {

    override fun inflateView() = R.layout.fragment_mine

    override fun init(savedInstanceState: Bundle?) {
        tv_name.text = BaseApplication.USER_INFO?.username
        tv_my_column.setOnClickListener(this)
    }

    override fun visibleToUser() {
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.login)
    }

    override fun onClick(v: View?) {
        when (v) {
            tv_my_column -> startActivity<SettingActivity>()
        }
    }

}