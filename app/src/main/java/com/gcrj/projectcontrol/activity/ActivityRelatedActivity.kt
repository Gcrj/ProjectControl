package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.adapter.ActivityRelatedAdapter
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.bean.ActivityRelatedBean
import com.gcrj.projectcontrol.bean.RefreshProgress
import com.gcrj.projectcontrol.bean.ResponseBean
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
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_related.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call

class ActivityRelatedActivity : BaseActivity(), LoadingLayout.OnRetryListener, SwipeRefreshLayout.OnRefreshListener {

    private val subProjectId by lazy {
        intent.getIntExtra("sub_project_id", 0)
    }
    private val activityId by lazy {
        intent.getIntExtra("activity_id", 0)
    }
    private val canEdit by lazy {
        intent.getBooleanExtra(Constant.CAN_EDIT, true)
    }

    private val adapter by lazy {
        ActivityRelatedAdapter(canEdit)
    }

    private var position = -1

    private val dialog by lazy {
        ProgressDialog(this)
    }
    private val confirmDialog by lazy {
        AlertDialog.Builder(this).setMessage("确认删除吗").setPositiveButton("确认") { _, _ ->
            dialog.show()
            RetrofitManager.apiService.deleteubActivityRelated(subProjectId, adapter.data[position].id).enqueue(nothingCallback)
        }.setNegativeButton("取消", null).create()
    }
    private val modifyDialog by lazy {
        etName = EditText(this)
        etName.post {
            (etName.parent as? View)?.setPadding(Tool.dp2px(20F).toInt(), 0, Tool.dp2px(20F).toInt(), 0)
        }
        AlertDialog.Builder(this).setTitle(R.string.modify_name).setView(etName).setPositiveButton("确认") { _, _ ->
            dialog.show()
            RetrofitManager.apiService.updateActivityRelated(adapter.data[position].id, etName.text.toString()).enqueue(nothingCallback)
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

    private val gson by lazy {
        Gson()
    }

    private var call: Call<ResponseBean<Nothing>>? = null
    private var list: List<ActivityRelatedBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_related)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        swipe_refresh_layout.setOnRefreshListener(this)
        loading_layout.setOnRetryListener(this)
        if (canEdit) {
            registerForContextMenu(recycler_view)
        }

        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private fun getData() {
        closeContextMenu()
        RetrofitManager.apiService.activityRelatedList(activityId).enqueue(callback)
    }

    override fun onRefresh() {
        getData()
    }

    override fun onRetry(state: Int) {
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private val callback by lazy {
        object : ResponseCallback<List<ActivityRelatedBean>>() {

            override fun onStart() = !isDestroyed

            override fun onSuccess(data: List<ActivityRelatedBean>) {
                if (data.isEmpty()) {
                    loading_layout.state = LoadingLayout.EMPTY
                    return
                }

                if (adapter.data.isEmpty()) {
                    recycler_view.layoutManager = LinearLayoutManager(this@ActivityRelatedActivity)
                    recycler_view.adapter = adapter
                    val divider = RecycleViewDivider(this@ActivityRelatedActivity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL)
                    recycler_view.addItemDecoration(divider)
                    adapter.setOnItemLongClickListener { _, _, position ->
                        this@ActivityRelatedActivity.position = position
                        false
                    }
                    loading_layout.state = LoadingLayout.SUCCESS
                }

                adapter.setNewData(data)
                list = data
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

    private val modifyCallback = object : NothingResponseCallback<Nothing>() {

        override fun onStart() = !isDestroyed

        override fun onSuccess() {
            ToastUtils.showToast("修改成功")
            EventBus.getDefault().post(RefreshProgress.INSTANCE)
            finish()
        }

        override fun onError(message: String) {
            ToastUtils.showToast(message)
        }

        override fun onNoNet(message: String) {
            ToastUtils.showToast(message)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_activity_related, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) = canEdit

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.option_menu_new_activity_related -> {
                startActivity<NewActivityRelatedActivity> {
                    it.putExtra(Constant.CAN_EDIT, canEdit)
                    it.putExtra(Constant.ACTIONBAR_TITLE, intent.getStringExtra(Constant.ACTIONBAR_TITLE))
                    it.putExtra("sub_project_id", subProjectId)
                    it.putExtra("activity_id", activityId)
                }
            }
            R.id.option_menu_submit -> {
                if (list?.isNotEmpty() != true) {
                    ToastUtils.showToast("没有内容")
                    return true
                }

                call = RetrofitManager.apiService.modifyActivityRelated(subProjectId, gson.toJson(list))
                call?.enqueue(modifyCallback)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.context_menu_activity_related, menu)
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
        call?.cancel()
        EventBus.getDefault().unregister(this)
    }

}
