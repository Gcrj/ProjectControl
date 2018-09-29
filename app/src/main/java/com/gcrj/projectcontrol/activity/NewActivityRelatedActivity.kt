package com.gcrj.projectcontrol.activity

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.bean.RefreshProgress
import com.gcrj.projectcontrol.http.NothingResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.view.ProgressDialog
import kotlinx.android.synthetic.main.activity_new_related.*
import org.greenrobot.eventbus.EventBus

class NewActivityRelatedActivity : BaseActivity(), View.OnClickListener {

    private val subProjectId by lazy {
        intent.getIntExtra("sub_project_id", 0)
    }
    private val activityId by lazy {
        intent.getIntExtra("activity_id", 0)
    }

    private val dialog by lazy {
        ProgressDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_related)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btn_add.setOnClickListener(this)
        btn_new_activity_related.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_new_activity_related -> {
                val sb = StringBuilder()
                (0 until ll_parent.childCount).forEach {
                    val child = ll_parent.getChildAt(it)
                    if (child is CheckBox) {
                        if (child.isChecked) {
                            sb.append(",").append(child.text.toString())
                        }
                    }
                }

                if (sb.isEmpty()) {
                    ToastUtils.showToast(tv_hint.text.toString())
                    return
                }

                val type = sb.deleteCharAt(0).toString()
                dialog.show()
                RetrofitManager.apiService.addActivityRelated(subProjectId, activityId, type).enqueue(callback)
            }
            btn_add -> {
                val other = et_other.text.toString().trim()
                if (other == "") {
                    return
                }

                val cb = CheckBox(this)
                cb.isChecked = true
                cb.text = other
                ll_parent.addView(cb, ll_parent.childCount - 2)
                et_other.text = null
            }
        }
    }

    private val callback by lazy {
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
