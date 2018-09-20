package com.gcrj.projectcontrol.fragment


import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.adapter.ProjectAdapter
import com.gcrj.projectcontrol.base.BaseFragment
import com.gcrj.projectcontrol.bean.ProjectBean
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.view.LoadingLayout
import com.gcrj.projectcontrol.viewRelated.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_project.*

class ProjectFragment : BaseFragment(), LoadingLayout.OnRetryListener, SwipeRefreshLayout.OnRefreshListener {

    override fun inflateView() = R.layout.fragment_project

    override fun visibleToUser() {
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.project)
    }

    private val adapter by lazy {
        ProjectAdapter()
    }

    override fun init(savedInstanceState: Bundle?) {
        loading_layout.setOnRetryListener(this)
        swipe_refresh_layout.setOnRefreshListener(this)
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

    override fun onRefresh() {
        getData()
    }

    private val callback by lazy {
        object : ResponseCallback<List<ProjectBean>>() {

            override fun onStart() = view != null

            override fun onSuccess(data: List<ProjectBean>) {
                if (data.isEmpty()) {
                    loading_layout.state = LoadingLayout.EMPTY
                    return
                }

                if (adapter.data.isEmpty()) {
                    recycler_view.layoutManager = LinearLayoutManager(context)
                    recycler_view.adapter = adapter
                    val divider = RecycleViewDivider(context, LinearLayoutManager.HORIZONTAL)
                    recycler_view.addItemDecoration(divider)
                    adapter.setOnItemClickListener { _, _, position ->
                        ToastUtils.showToast(adapter.data[position].name)
                    }
                    loading_layout.state = LoadingLayout.SUCCESS
                }

                adapter.setNewData(data)
            }

            override fun onError(message: String) {
                if (adapter.data.isEmpty()) {
                    loading_layout.state = LoadingLayout.FAILED
                }

                ToastUtils.showToast(message)
            }

            override fun onNoNet(message: String) {
                if (adapter.data.isEmpty()) {
                    loading_layout.state = LoadingLayout.NO_NET
                }

                ToastUtils.showToast(message)
            }

            override fun onAfter() {
                if (swipe_refresh_layout.isRefreshing) {
                    swipe_refresh_layout.isRefreshing = false
                }
            }
        }
    }

}
