package com.gcrj.projectcontrol.fragment


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseFragment
import com.gcrj.projectcontrol.bean.ProjectBean
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.view.LoadingLayout
import kotlinx.android.synthetic.main.fragment_project.*

class ProjectFragment : BaseFragment(), LoadingLayout.OnRetryListener {

    override fun inflateView() = R.layout.fragment_project

    override fun visibleToUser() {
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.project)
    }

    override fun init(savedInstanceState: Bundle?) {
        loading_layout.setOnRetryListener(this)
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private fun getData() {
        RetrofitManager.apiService.projectList().enqueue(callback)
    }

    override fun onRetry(state: Int) {
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private val callback by lazy {
        object : ResponseCallback<List<ProjectBean>>() {

            override fun onStart() = view != null

            override fun onSuccess(data: List<ProjectBean>) {
                loading_layout.state = LoadingLayout.EMPTY
            }

            override fun onError(message: String) {
                loading_layout.state = LoadingLayout.FAILED
                ToastUtils.showToast(message)
            }

            override fun onNoNet(message: String) {
                loading_layout.state = LoadingLayout.NO_NET
                ToastUtils.showToast(message)
            }
        }
    }

}
