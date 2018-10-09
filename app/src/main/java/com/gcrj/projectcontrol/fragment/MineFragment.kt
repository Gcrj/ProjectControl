package com.gcrj.projectcontrol.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gcrj.projectcontrol.BaseApplication
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.activity.PreviewXlsProjectActivity
import com.gcrj.projectcontrol.activity.SettingActivity
import com.gcrj.projectcontrol.base.BaseFragment
import com.gcrj.projectcontrol.util.startActivity
import kotlinx.android.synthetic.main.fragment_mine.*

class MineFragment : BaseFragment(), View.OnClickListener {

    override fun inflateView() = R.layout.fragment_mine

    override fun init(savedInstanceState: Bundle?) {
        tv_name.text = BaseApplication.USER_INFO?.username
        tv_preview_and_submit_xls.setOnClickListener(this)
        tv_my_setting.setOnClickListener(this)
    }

    override fun visibleToUser() {
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.mine)
    }

    override fun onClick(v: View?) {
        when (v) {
            tv_preview_and_submit_xls -> startActivity<PreviewXlsProjectActivity>()
            tv_my_setting -> startActivity<SettingActivity>()
        }
    }

}