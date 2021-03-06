package com.gcrj.projectcontrol.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
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
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class NewSubProjectActivity : BaseActivity(), View.OnClickListener, LoadingLayout.OnRetryListener {

    private val dialog by lazy {
        ProgressDialog(this)
    }

    private val calendar = Calendar.getInstance()
    private var deadline = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    private val dataPickerDialog by lazy {
        val dataPickerDialog = DatePickerDialog(this@NewSubProjectActivity, 0, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            var realMonth = (month + 1).toString()
            if (realMonth.length == 1) {
                realMonth = "0$realMonth"
            }
            var realDayOfMonth = dayOfMonth.toString()
            if (realDayOfMonth.length == 1) {
                realDayOfMonth = "0$realDayOfMonth"
            }

            deadline = "$year-$realMonth-$realDayOfMonth"
            date_picker.text = "截止日期：$deadline"
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        dataPickerDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_sub_project)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        date_picker.text = "截止日期：$deadline"
        date_picker.setOnClickListener {
            dataPickerDialog.show()
        }
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
                data.forEach {
                    val checkBox = CheckBox(this@NewSubProjectActivity)
                    checkBox.text = it.name
                    checkBox.tag = it
                    flow_group_view.addView(checkBox)
                }
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

            var bean: ProjectBean? = null
            run {
                (0 until flow_group_view.childCount).forEach {
                    val child = flow_group_view.getChildAt(it) as CheckBox
                    if (child.isChecked) {
                        bean = child.tag as ProjectBean
                        return@run
                    }
                }
            }

            if (bean == null) {
                ToastUtils.showToast("请选择所属项目")
                return
            }

            dialog.show()
            RetrofitManager.apiService.addSubProject(name, bean?.id
                    ?: 0, deadline).enqueue(newSubProjectCallback)
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
