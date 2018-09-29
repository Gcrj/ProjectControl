package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.bean.ProjectBean
import com.gcrj.projectcontrol.bean.RefreshProgress
import com.gcrj.projectcontrol.http.NothingResponseCallback
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.view.LoadingLayout
import com.gcrj.projectcontrol.view.ProgressDialog
import kotlinx.android.synthetic.main.activity_new_sub_project.*
import org.greenrobot.eventbus.EventBus

class NewSubProjectActivity : BaseActivity(), View.OnClickListener, LoadingLayout.OnRetryListener {

    private lateinit var data: List<ProjectBean>
    private val dialog by lazy {
        val dialog = ProgressDialog(this)
        dialog.setCancelable(false)
        dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_sub_project)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btn_new_sub_project.setOnClickListener(this)
        loading_layout.setOnRetryListener(this)
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private fun getData() {
        RetrofitManager.apiService.projectList().enqueue(projectListCallback)
    }

    private val projectListCallback by lazy {
        object : ResponseCallback<List<ProjectBean>>() {

            override fun onStart() = !isDestroyed

            override fun onSuccess(data: List<ProjectBean>) {
                if (data.isEmpty()) {
                    loading_layout.state = LoadingLayout.EMPTY
                    return
                }

                loading_layout.state = LoadingLayout.SUCCESS
                this@NewSubProjectActivity.data = data
                val list = mutableListOf("请选择所属项目")
                list.addAll(data.map { it.name ?: "" })
                spinner.adapter = ArrayAdapter(this@NewSubProjectActivity, android.R.layout.simple_spinner_item, android.R.id.text1, list)
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

    override fun onRetry(state: Int) {
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    override fun onClick(v: View?) {
        if (v == btn_new_sub_project) {
            val name = et_sub_project_name.text.toString().trim()
            if (name == "") {
                ToastUtils.showToast(til_sub_project_name.hint.toString())
                return
            }

            val position = spinner.selectedItemPosition
            if (position == 0) {
                ToastUtils.showToast("请选择所属项目")
                return
            }


            dialog.show()
            RetrofitManager.apiService.addSubProject(name, data[position - 1].id
                    ?: 0).enqueue(newSubProjectCallback)
        }
    }

    private val newSubProjectCallback by lazy {
        object : NothingResponseCallback<Nothing>() {

            override fun onStart() = !isDestroyed

            override fun onSuccess() {
                ToastUtils.showToast("添加成功")
                EventBus.getDefault().post(RefreshProgress.INSTANCE)
                finish()
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

}
