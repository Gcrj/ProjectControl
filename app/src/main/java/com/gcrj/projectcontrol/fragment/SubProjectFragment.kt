package com.gcrj.projectcontrol.fragment


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.activity.ActivityActivity
import com.gcrj.projectcontrol.activity.NewSubProjectActivity
import com.gcrj.projectcontrol.adapter.SubProjectAdapter
import com.gcrj.projectcontrol.base.BaseFragment
import com.gcrj.projectcontrol.bean.RefreshProgress
import com.gcrj.projectcontrol.bean.SubProjectBean
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.Constant
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.util.startActivity
import com.gcrj.projectcontrol.view.LoadingLayout
import com.gcrj.projectcontrol.viewRelated.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_sub_project.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class SubProjectFragment : BaseFragment(), LoadingLayout.OnRetryListener, SwipeRefreshLayout.OnRefreshListener {

    override fun inflateView() = R.layout.fragment_sub_project

    override fun visibleToUser() {
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.sub_project)
    }

    private val adapter by lazy {
        SubProjectAdapter()
    }

    override fun init(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        setHasOptionsMenu(true)
        loading_layout.setOnRetryListener(this)
        swipe_refresh_layout.setOnRefreshListener(this)
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private fun getData() {
        RetrofitManager.apiService.subProjectListByUser().enqueue(callback)
    }

    override fun onRetry(state: Int) {
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    override fun onRefresh() {
        getData()
    }

    private val callback by lazy {
        object : ResponseCallback<List<SubProjectBean>>() {

            override fun onStart() = view != null

            override fun onSuccess(data: List<SubProjectBean>) {
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
                        startActivity<ActivityActivity> {
                            val bean = adapter.data[position]
                            it.putExtra(Constant.CAN_EDIT, bean.completionTime == null)
                            it.putExtra(Constant.ACTIONBAR_TITLE, bean.name)
                            it.putExtra("sub_project_id", bean.id)
                        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sub_project, menu);
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_sub_project -> {
                startActivity<NewSubProjectActivity>()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @Subscribe()
    fun handleEvent(refreshProgress: RefreshProgress) {
        if (loading_layout.state == LoadingLayout.SUCCESS) {
            swipe_refresh_layout.isRefreshing = true
        } else {
            loading_layout.state = LoadingLayout.LOADING
        }

        getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

}
