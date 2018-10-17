package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.fragment.app.transaction
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.fragment.MineFragment
import com.gcrj.projectcontrol.fragment.ProjectFragment
import com.gcrj.projectcontrol.fragment.SubProjectFragment
import com.gcrj.projectcontrol.util.AppTool
import com.gcrj.projectcontrol.util.startActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemReselectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private var subProjectFragment: SubProjectFragment? = null
    private var projectFragment: ProjectFragment? = null
    private var mineFragment: MineFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        swipeFinishEnable = false
        setContentView(R.layout.activity_main)
        bnv.setOnNavigationItemSelectedListener(this)
        bnv.itemIconSize = ViewGroup.LayoutParams.WRAP_CONTENT
        if (savedInstanceState == null) {
            subProjectFragment = SubProjectFragment()
            supportFragmentManager.beginTransaction().add(R.id.fl_container, subProjectFragment!!).commitAllowingStateLoss()
        } else {
            supportFragmentManager.fragments.forEach {
                when (it) {
                    is SubProjectFragment -> subProjectFragment = it
                    is ProjectFragment -> projectFragment = it
                    is MineFragment -> mineFragment = it
                }
            }
        }

        AppTool.checkUpdate { bean, msg ->
            if (bean?.hasUpdate == true) {
                startActivity<UpdateActivity> {
                    it.putExtra("bean", bean)
                }
            }
//            else {
//                ToastUtils.showToast(msg ?: "已经是最新版本")
//            }
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        supportFragmentManager.transaction(allowStateLoss = true) {
            subProjectFragment?.let {
                detach(it)
            }
            projectFragment?.let {
                detach(it)
            }
            mineFragment?.let {
                detach(it)
            }

            when (menuItem.itemId) {
                R.id.option_menu_sub_project -> attach(subProjectFragment!!)
                R.id.option_menu_project -> {
                    if (projectFragment == null) {
                        projectFragment = ProjectFragment()
                        add(R.id.fl_container, projectFragment!!)
                    } else {
                        attach(projectFragment!!)
                    }
                }
                R.id.option_menu_mine -> {
                    if (mineFragment == null) {
                        mineFragment = MineFragment()
                        add(R.id.fl_container, mineFragment!!)
                    } else {
                        attach(mineFragment!!)
                    }
                }
            }
        }

        return true
    }

    override fun onNavigationItemReselected(menuItem: MenuItem) {

    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

}
