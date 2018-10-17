package com.gcrj.projectcontrol.activity

import android.app.DatePickerDialog
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
import com.gcrj.projectcontrol.adapter.SubProjectAdapter
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.bean.RefreshProgress
import com.gcrj.projectcontrol.bean.SubProjectBean
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
import kotlinx.android.synthetic.main.activity_sub_project.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class SubProjectActivity : BaseActivity(), LoadingLayout.OnRetryListener, SwipeRefreshLayout.OnRefreshListener {

    private val projectId by lazy {
        intent.getIntExtra("project_id", 0)
    }

    private val adapter by lazy {
        SubProjectAdapter()
    }
    private var position = -1

    private val dialog by lazy {
        ProgressDialog(this)
    }
    private var type: Int? = null
    private val confirmDialog by lazy {
        AlertDialog.Builder(this).setMessage("确认删除吗").setPositiveButton("确认") { _, _ ->
            dialog.show()
            RetrofitManager.apiService.deleteubProject(adapter.data[position].id).enqueue(nothingCallback)
        }.setNegativeButton("取消", null).create()
    }
    private val dataPickerDialog by lazy {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, 0, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            var realMonth = (month + 1).toString()
            if (realMonth.length == 1) {
                realMonth = "0$realMonth"
            }
            var realDayOfMonth = dayOfMonth.toString()
            if (realDayOfMonth.length == 1) {
                realDayOfMonth = "0$realDayOfMonth"
            }

            val bean = adapter.data[position]
            when (type) {
                R.id.context_menu_deadline -> {
                    dialog.show()
                    RetrofitManager.apiService.updateSubProject(id = bean.id, deadline = "$year-$realMonth-$realDayOfMonth").enqueue(nothingCallback)
                }
                R.id.context_menu_complete_time -> {
                    dialog.show()
                    RetrofitManager.apiService.updateSubProject(id = bean.id, completionTime = "$year-$realMonth-$realDayOfMonth").enqueue(nothingCallback)
                }
            }
            view.updateDate(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
    }
    private val modifyDialog by lazy {
        etName = EditText(this)
        etName.post {
            (etName.parent as? View)?.setPadding(Tool.dp2px(20F).toInt(), 0, Tool.dp2px(20F).toInt(), 0)
        }
        AlertDialog.Builder(this).setView(etName).setPositiveButton("确认") { _, _ ->
            val bean = adapter.data[position]
            when (type) {
                R.id.context_menu_modify_name -> {
                    dialog.show()
                    RetrofitManager.apiService.updateSubProject(id = bean.id, name = etName.text.toString()).enqueue(nothingCallback)
                }
                R.id.context_menu_confirm_version_name -> {
                    dialog.show()
                    RetrofitManager.apiService.updateSubProject(id = bean.id, versionName = etName.text.toString()).enqueue(nothingCallback)
                }
            }
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
        setContentView(R.layout.activity_sub_project)
        EventBus.getDefault().register(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loading_layout.setOnRetryListener(this)
        swipe_refresh_layout.setOnRefreshListener(this)
        registerForContextMenu(recycler_view)

        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private fun getData() {
        closeContextMenu()
        RetrofitManager.apiService.subProjectListByProject(projectId).enqueue(callback)
    }

    private val callback by lazy {
        object : ResponseCallback<List<SubProjectBean>>() {

            override fun onStart() = !isDestroyed

            override fun onSuccess(data: List<SubProjectBean>) {
                if (data.isEmpty()) {
                    loading_layout.state = LoadingLayout.EMPTY
                    return
                }

                if (adapter.data.isEmpty()) {
                    recycler_view.layoutManager = LinearLayoutManager(this@SubProjectActivity)
                    recycler_view.adapter = adapter
                    val divider = RecycleViewDivider(this@SubProjectActivity, LinearLayoutManager.HORIZONTAL)
                    recycler_view.addItemDecoration(divider)
                    adapter.setOnItemClickListener { _, _, position ->
                        startActivity<ActivityActivity> {
                            val bean = adapter.data[position]
//                            it.putExtra(Constant.CAN_EDIT, bean.completionTime == null)
                            it.putExtra(Constant.ACTIONBAR_TITLE, bean.name)
                            it.putExtra("sub_project_id", bean.id)
                        }
                    }
                    adapter.setOnItemLongClickListener { _, _, position ->
                        this@SubProjectActivity.position = position
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

    override fun onRetry(state: Int) {
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    override fun onRefresh() {
        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_sub_project, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.option_menu_new_sub_project -> {
                startActivity<NewSubProjectActivity>()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.context_menu_sub_project, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        type = item?.itemId
        when (type) {
            R.id.context_menu_modify_name -> {
                modifyDialog.setTitle(getString(R.string.modify_name))
                modifyDialog.show()
                etName.setText(adapter.data[position].name)
                return true
            }
            R.id.context_menu_delete -> {
                confirmDialog.show()
                return true
            }
            R.id.context_menu_deadline -> {
                dataPickerDialog.show()
                return true
            }
            R.id.context_menu_complete_time -> {
                dataPickerDialog.show()
                return true
            }
            R.id.context_menu_confirm_version_name -> {
                modifyDialog.setTitle(getString(R.string.confirm_version_name))
                modifyDialog.show()
                etName.setText(adapter.data[position].versionName)
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
