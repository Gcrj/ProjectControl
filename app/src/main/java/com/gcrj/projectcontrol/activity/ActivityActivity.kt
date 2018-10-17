package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.adapter.ActivityAdapter
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.bean.ActivityBean
import com.gcrj.projectcontrol.bean.RefreshProgress
import com.gcrj.projectcontrol.http.NothingResponseCallback
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.Constant
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.util.Tool
import com.gcrj.projectcontrol.util.startActivity
import com.gcrj.projectcontrol.view.LoadingLayout
import com.gcrj.projectcontrol.view.ProgressDialog
import com.gcrj.projectcontrol.viewRelated.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ActivityActivity : BaseActivity(), LoadingLayout.OnRetryListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {

    private val subProjectId by lazy {
        intent.getIntExtra("sub_project_id", 0)
    }
    private val canEdit by lazy {
        intent.getBooleanExtra(Constant.CAN_EDIT, true)
    }

    private val adapter by lazy {
        ActivityAdapter(canEdit)
    }

    private var position = -1

    private val dialog by lazy {
        ProgressDialog(this)
    }
    private val confirmDialog by lazy {
        AlertDialog.Builder(this).setMessage("确认删除吗").setPositiveButton("确认") { _, _ ->
            dialog.show()
            RetrofitManager.apiService.deleteubActivity(subProjectId, adapter.data[position].id).enqueue(nothingCallback)
        }.setNegativeButton("取消", null).create()
    }
    private val modifyDialog by lazy {
        etName = EditText(this)
        etName.post {
            (etName.parent as? View)?.setPadding(Tool.dp2px(20F).toInt(), 0, Tool.dp2px(20F).toInt(), 0)
        }
        AlertDialog.Builder(this).setTitle(R.string.modify_name).setView(etName).setPositiveButton("确认") { _, _ ->
            dialog.show()
            RetrofitManager.apiService.updateActivity(adapter.data[position].id, etName.text.toString()).enqueue(nothingCallback)
        }.setNegativeButton("取消", null).create()
    }
    private lateinit var etName: EditText
    private val nothingCallback by lazy {
        object : NothingResponseCallback<Nothing>() {

            override fun onStart() = !isDestroyed

            override fun onSuccess() {
                ToastUtils.showToast("操作成功")
                EventBus.getDefault().post(RefreshProgress.INSTANCE)
                swipe_refresh_layout.isRefreshing = true
                getData()
            }

            override fun onError(message: String) {
                ToastUtils.showToast(message)
            }

            override fun onNoNet(message: String) {
                ToastUtils.showToast(message)
            }

            override fun onAfter() {
                dialog.dismiss()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loading_layout.setOnRetryListener(this)
        swipe_refresh_layout.setOnRefreshListener(this)
        if (canEdit) {
            registerForContextMenu(recycler_view)
        }

        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private fun getData() {
        closeContextMenu()
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
                    recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@ActivityActivity)
                    recycler_view.adapter = adapter
                    val divider = RecycleViewDivider(this@ActivityActivity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL)
                    recycler_view.addItemDecoration(divider)
                    adapter.setOnItemClickListener { _, _, position ->
                        startActivity<ActivityRelatedActivity> {
                            val bean = adapter.data[position]
                            it.putExtra(Constant.CAN_EDIT, canEdit)
                            it.putExtra(Constant.ACTIONBAR_TITLE, bean.name)
                            it.putExtra("sub_project_id", bean.sub_project_id)
                            it.putExtra("activity_id", bean.id)
                        }
                    }
                    adapter.setOnItemLongClickListener { _, _, position ->
                        this@ActivityActivity.position = position
                        false
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
        menuInflater.inflate(R.menu.option_menu_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) = canEdit

    override
    fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.option_menu_new_activity -> {
                startActivity<NewActivityActivity> {
                    it.putExtra("sub_project_id", subProjectId)
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.context_menu_activity, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.context_menu_modify_name -> {
                modifyDialog.show()
                etName.setText(adapter.data[position].name)
                return true
            }
            R.id.context_menu_delete -> {
                confirmDialog.show()
                return true
            }
        }

        return super.onContextItemSelected(item)
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
