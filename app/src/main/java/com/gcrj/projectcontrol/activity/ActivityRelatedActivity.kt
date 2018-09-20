package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.widget.TextView
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.adapter.ActivityRelatedAdapter
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.bean.ActivityRelatedBean
import com.gcrj.projectcontrol.bean.RefreshProgress
import com.gcrj.projectcontrol.bean.ResponseBean
import com.gcrj.projectcontrol.http.NothingResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.viewRelated.RecycleViewDivider
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_related.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call

class ActivityRelatedActivity : BaseActivity() {

    private val subProjectId by lazy {
        intent.getIntExtra("sub_project_id", 0)
    }
    private val list by lazy {
        intent.getParcelableArrayListExtra<ActivityRelatedBean>("list")
    }

    private val gson by lazy {
        Gson()
    }
    private var call: Call<ResponseBean<Nothing>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_related)
        val toolbar = findViewById<Toolbar?>(android.support.v7.appcompat.R.id.action_bar)
        (toolbar?.getChildAt(0) as? TextView)?.let { it ->
            val tv = TextView(this)
            tv.text = "提交"
            tv.gravity = Gravity.CENTER
            tv.setTextColor(it.textColors)
            tv.setOnClickListener {
                call = RetrofitManager.apiService.modifyActivityRelated(subProjectId, gson.toJson(list))
                call?.enqueue(callback)
            }

            val lp = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT)
            lp.gravity = Gravity.END
            toolbar.addView(tv, lp)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler_view.layoutManager = LinearLayoutManager(this)
        val adapter = ActivityRelatedAdapter()
        adapter.setNewData(list)
        recycler_view.adapter = adapter
        val divider = RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL)
        recycler_view.addItemDecoration(divider)
    }

    private val callback = object : NothingResponseCallback<Nothing>() {

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

    override fun onDestroy() {
        super.onDestroy()
            call?.cancel()
    }

}
