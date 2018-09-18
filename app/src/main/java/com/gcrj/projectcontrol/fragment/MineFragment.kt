package com.gcrj.projectcontrol.fragment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.gcrj.projectcontrol.BaseApplication
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.activity.LoginActivity
import com.gcrj.projectcontrol.activity.SettingActivity
import com.gcrj.projectcontrol.base.BaseFragment
import com.gcrj.projectcontrol.bean.LoginBean
import com.gcrj.projectcontrol.util.startActivity
import kotlinx.android.synthetic.main.fragment_mine.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MineFragment : BaseFragment(), View.OnClickListener {

    override fun inflateView() = R.layout.fragment_mine

    override fun init(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        if (BaseApplication.USER_INFO == null) {
            tv_name.visibility = View.INVISIBLE
            btn_login.visibility = View.VISIBLE
            btn_login.setOnClickListener(this)
        } else {
            dealLogin()
        }

        tv_my_column.setOnClickListener(this)
    }

    override fun visibleToUser() {
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.login)
    }

    private fun dealLogin() {
        tv_name.visibility = View.VISIBLE
        btn_login.visibility = View.INVISIBLE
        tv_name.text = BaseApplication.USER_INFO?.username
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_login -> startActivity<LoginActivity>()
            tv_my_column -> startActivity<SettingActivity>()
        }
    }

    @Subscribe()
    fun loginSuccess(event: LoginBean) {
        dealLogin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

}