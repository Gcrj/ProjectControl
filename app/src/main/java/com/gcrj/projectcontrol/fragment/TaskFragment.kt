package com.gcrj.projectcontrol.fragment


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseFragment

class TaskFragment : BaseFragment() {

    override fun inflateView() = R.layout.fragment_task

    override fun init(savedInstanceState: Bundle?) {
    }

    override fun visibleToUser() {
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.task)
    }

}
