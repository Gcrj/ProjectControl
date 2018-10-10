package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.ViewGroup
import androidx.fragment.app.transaction
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.fragment.MineFragment
import com.gcrj.projectcontrol.fragment.ProjectFragment
import com.gcrj.projectcontrol.fragment.SubProjectFragment
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
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        supportFragmentManager.transaction(allowStateLoss = true) {
            subProjectFragment?.let {
                hide(it)
            }
            projectFragment?.let {
                hide(it)
            }
            mineFragment?.let {
                hide(it)
            }

            when (menuItem.itemId) {
                R.id.menu_sub_project -> show(subProjectFragment!!)
                R.id.menu_project -> {
                    if (projectFragment == null) {
                        projectFragment = ProjectFragment()
                        add(R.id.fl_container, projectFragment!!)
                    } else {
                        show(projectFragment!!)
                    }
                }
                R.id.menu_mine -> {
                    if (mineFragment == null) {
                        mineFragment = MineFragment()
                        add(R.id.fl_container, mineFragment!!)
                    } else {
                        show(mineFragment!!)
                    }
                }
            }
        }

        return true
    }

    override fun onNavigationItemReselected(menuItem: MenuItem) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

}
