package com.gcrj.projectcontrol.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.adapter.ActivityAdapter
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.bean.ActivityBean
import com.gcrj.projectcontrol.bean.RefreshProgress
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.Constant
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.util.startActivity
import com.gcrj.projectcontrol.util.startActivityForResult
import com.gcrj.projectcontrol.view.LoadingLayout
import com.gcrj.projectcontrol.viewRelated.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class ActivityActivity : BaseActivity(), LoadingLayout.OnRetryListener, SwipeRefreshLayout.OnRefreshListener {

    companion object {
        private const val REQUEST_CODE_NEW_ACTIVITY = 1
    }

    private val subProjectId by lazy {
        intent.getIntExtra("sub_project_id", 0)
    }
    private val adapter by lazy {
        ActivityAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        swipe_refresh_layout.setOnRefreshListener(this)
        loading_layout.setOnRetryListener(this)
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private fun getData() {
        RetrofitManager.apiService.activityList(subProjectId).enqueue(callback)
    }

    override fun onRefresh() {
        getData()
    }

    override fun onRetry(state: Int) {
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private val callback by lazy {
        object : ResponseCallback<List<ActivityBean>>() {

            override fun onStart() = !isDestroyed

            override fun onSuccess(data: List<ActivityBean>) {
                if (data.isEmpty()) {
                    loading_layout.state = LoadingLayout.EMPTY
                    return
                }

                if (adapter.data.isEmpty()) {
                    recycler_view.layoutManager = LinearLayoutManager(this@ActivityActivity)
                    recycler_view.adapter = adapter
                    val divider = RecycleViewDivider(this@ActivityActivity, LinearLayoutManager.HORIZONTAL)
                    recycler_view.addItemDecoration(divider)
                    adapter.setOnItemClickListener { _, _, position ->
                        startActivity<ActivityRelatedActivity> {
                            it.putExtra(Constant.ACTIONBAR_TITLE, adapter.data[position].name)
                            it.putExtra("sub_project_id", adapter.data[position].sub_project_id)
                            it.putParcelableArrayListExtra("list", adapter.data[position].activityRelated as ArrayList)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_activity -> {
                startActivityForResult<NewActivityActivity>({
                    it.putExtra("sub_project_id", subProjectId)
                }, REQUEST_CODE_NEW_ACTIVITY)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NEW_ACTIVITY && resultCode == Activity.RESULT_OK) {
            swipe_refresh_layout.isRefreshing = true
            getData()
        }
    }

    @Subscribe()
    fun handleEvent(refreshProgress: RefreshProgress) {
        swipe_refresh_layout.isRefreshing = true
        getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
