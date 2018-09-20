package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.KeyEvent
import android.view.MenuItem
import android.view.ViewGroup
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.fragment.MineFragment
import com.gcrj.projectcontrol.fragment.ProjectFragment
import com.gcrj.projectcontrol.fragment.TaskFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemReselectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private var taskFragment: TaskFragment? = null
    private var projectFragment: ProjectFragment? = null
    private var mineFragment: MineFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bnv.setOnNavigationItemSelectedListener(this)
        bnv.itemIconSize = ViewGroup.LayoutParams.WRAP_CONTENT
        if (savedInstanceState == null) {
            taskFragment = TaskFragment()
            supportFragmentManager.beginTransaction().add(R.id.fl_container, taskFragment!!).commitAllowingStateLoss()
        } else {
            supportFragmentManager.fragments.forEach {
                when (it) {
                    is TaskFragment -> taskFragment = it
                    is ProjectFragment -> projectFragment = it
                    is MineFragment -> mineFragment = it
                }
            }
        }

    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        taskFragment?.let {
            transaction.hide(it)
        }
        projectFragment?.let {
            transaction.hide(it)
        }
        mineFragment?.let {
            transaction.hide(it)
        }

        when (menuItem.itemId) {
            R.id.menu_task -> transaction.show(taskFragment!!)
            R.id.menu_project -> {
                if (projectFragment == null) {
                    projectFragment = ProjectFragment()
                    transaction.add(R.id.fl_container, projectFragment!!)
                } else {
                    transaction.show(projectFragment!!)
                }
            }
            R.id.menu_mine -> {
                if (mineFragment == null) {
                    mineFragment = MineFragment()
                    transaction.add(R.id.fl_container, mineFragment!!)
                } else {
                    transaction.show(mineFragment!!)
                }
            }
        }

        transaction.commitAllowingStateLoss()
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
