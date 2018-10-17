package com.gcrj.projectcontrol.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gcrj.projectcontrol.BuildConfig
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.adapter.PreviewXlsProjectAdapter
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.bean.ProjectBean
import com.gcrj.projectcontrol.bean.ResponseBean
import com.gcrj.projectcontrol.bean.XlsProjectBean
import com.gcrj.projectcontrol.http.NothingResponseCallback
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.util.startActivityForResult
import com.gcrj.projectcontrol.view.LoadingLayout
import com.gcrj.projectcontrol.view.ProgressDialog
import com.gcrj.projectcontrol.viewRelated.RecycleViewDivider
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_preview_xls_project.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*

class PreviewXlsProjectActivity : BaseActivity(), LoadingLayout.OnRetryListener {

    companion object {
        private const val REQUEST_CODE_CUSTOM = 1
    }

    private var previewCall: Call<ResponseBody>? = null
    private var submitCall: Call<ResponseBean<Nothing>>? = null

    private val dialog by lazy {
        val dialog = ProgressDialog(this)
        dialog
    }

    private val adapter by lazy {
        PreviewXlsProjectAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_xls_project)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loading_layout.setOnRetryListener(this)
        loading_layout.state = LoadingLayout.LOADING
        getData()
    }

    private fun getData() {
        RetrofitManager.apiService.previewXlsProjectList().enqueue(callback)
    }

    private val callback by lazy {
        object : ResponseCallback<List<ProjectBean>>() {

            override fun onStart() = !isDestroyed

            override fun onSuccess(data: List<ProjectBean>) {
                if (data.isEmpty()) {
                    loading_layout.state = LoadingLayout.EMPTY
                    return
                }

                recycler_view.layoutManager = LinearLayoutManager(this@PreviewXlsProjectActivity)
                recycler_view.adapter = adapter
                val divider = RecycleViewDivider(this@PreviewXlsProjectActivity, LinearLayoutManager.HORIZONTAL)
                recycler_view.addItemDecoration(divider)

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                val monday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

                adapter.setNewData(data.map { project ->
                    val bean = XlsProjectBean.parseXlsProjectBean(project)
                    var hasChecked = false
                    bean.subProject?.forEach { subProject ->
                        if (subProject.deadline != null && subProject.deadline!! >= monday) {
                            subProject.checked = true
                            subProject.activity?.forEach { activity ->
                                activity.checked = true
                                activity.activityRelated?.forEach { activityRelated ->
                                    activityRelated.checked = true
                                }
                            }

                            hasChecked = true
                        }
                    }
                    if (hasChecked) {
                        bean.checked = true
                    }

                    bean
                })
                loading_layout.state = LoadingLayout.SUCCESS
                invalidateOptionsMenu()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_preview_xls_project, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) = loading_layout.state == LoadingLayout.SUCCESS

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.option_menu_add_custom -> startActivityForResult<CustomXlsActivity>(REQUEST_CODE_CUSTOM)
            R.id.option_menu_preview -> {
                val filterData = adapter.data.asSequence().map { if (it.type == XlsProjectBean.TYPE_CUSTOM) it else XlsProjectBean.parseXlsProjectBean(it.clone()) }.filter {
                    it.checked
                }.toList()

                if (filterData.isEmpty()) {
                    ToastUtils.showToast("选中内容为空")
                    return true
                }

                dialog.setMessage("正在获取周报")
                dialog.show()
                filterData.filter {
                    it.type == XlsProjectBean.TYPE_PROJECT
                }.forEach { project ->
                    project.subProject = project.subProject?.filter {
                        it.checked
                    }

                    project.subProject?.forEach { subProject ->
                        subProject.activity = subProject.activity?.filter {
                            it.checked
                        }

                        subProject.activity?.forEach { activity ->
                            activity.activityRelated = activity.activityRelated?.filter {
                                it.checked
                            }
                        }
                    }
                }
                previewCall = RetrofitManager.apiService.previewXls(RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), Gson().toJson(filterData)))
                previewCall?.enqueue(previewCallback)
                return true
            }
            R.id.option_menu_submit -> {
                val filterData = adapter.data.asSequence().map { if (it.type == XlsProjectBean.TYPE_CUSTOM) it else XlsProjectBean.parseXlsProjectBean(it.clone()) }.filter {
                    it.checked
                }.toList()

                if (filterData.isEmpty()) {
                    ToastUtils.showToast("选中内容为空")
                    return true
                }

                dialog.setMessage("正在提交周报")
                dialog.show()
                filterData.filter {
                    it.type == XlsProjectBean.TYPE_PROJECT
                }.forEach { project ->
                    project.subProject = project.subProject?.filter {
                        it.checked
                    }

                    project.subProject?.forEach { subProject ->
                        subProject.activity = subProject.activity?.filter {
                            it.checked
                        }

                        subProject.activity?.forEach { activity ->
                            activity.activityRelated = activity.activityRelated?.filter {
                                it.checked
                            }
                        }
                    }
                }
                submitCall = RetrofitManager.apiService.submitXls(RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), Gson().toJson(filterData)))
                submitCall?.enqueue(submitCallback)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private val previewCallback by lazy {
        object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (isDestroyed) {
                    return
                }

                if (!response.isSuccessful || response.body() == null) {
                    ToastUtils.showToast("未知错误")
                    dialog.dismiss()
                    return
                }

                if (response.body()?.contentType()?.toString() == "application/json;charset=utf-8") {
                    try {
                        val body = response.body()
                        if (body == null) {
                            ToastUtils.showToast("body为空")
                            return
                        }

                        val responseBean = Gson().fromJson<ResponseBean<Nothing>>(body.string(), object : TypeToken<ResponseBean<Nothing>>() {}.type)
                        ToastUtils.showToast(responseBean.msg ?: "未知错误")
                    } catch (e: JsonParseException) {
                        ToastUtils.showToast("未知错误")
                    }

                    dialog.dismiss()
                    return
                }

                Thread(Runnable {
                    var fis: InputStream? = null
                    var fos: FileOutputStream? = null
                    try {
                        var name = response.headers()["Content-Disposition"]?.substring("attachment;filename=".length)
                        if (name != null) {
                            name = URLDecoder.decode(name, "utf-8")
                        } else {
                            name = "周报"
                        }

                        val file = File(this@PreviewXlsProjectActivity.cacheDir, name)
                        val body = response.body()
                        if (body == null) {
                            runOnUiThread {
                                dialog.dismiss()
                                ToastUtils.showToast("body为空")
                            }
                            return@Runnable
                        }

                        fis = body.byteStream()
                        fos = FileOutputStream(file)
                        val b = ByteArray(1024)
                        var read = fis.read(b)
                        while (read != -1) {
                            fos.write(b, 0, read)
                            read = fis.read(b)
                        }

                        fos.flush()
                        runOnUiThread {
                            dialog.dismiss()
                            val intent = Intent(Intent.ACTION_VIEW)
                            val uri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                Uri.fromFile(file)
                            } else {
                                FileProvider.getUriForFile(this@PreviewXlsProjectActivity, BuildConfig.APPLICATION_ID + ".provider", file)
                            }

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                            if (this@PreviewXlsProjectActivity.packageManager.queryIntentActivities(intent, 0).size > 0) {
                                startActivity(intent)
                            } else {
                                ToastUtils.showToast("未发现能打开此文件的应用")
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        runOnUiThread {
                            ToastUtils.showToast("IO异常")
                            dialog.dismiss()
                        }
                    } finally {
                        fis?.close()
                        fos?.close()
                    }
                }).start()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if (isDestroyed) {
                    return
                }

                ToastUtils.showToast("网络异常")
                dialog.dismiss()
            }

        }
    }

    private val submitCallback = object : NothingResponseCallback<Nothing>() {

        override fun onStart() = !isDestroyed

        override fun onSuccess() {
            ToastUtils.showToast("提交成功")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CUSTOM && resultCode == Activity.RESULT_OK && data != null) {
            val xlsProjectBean = XlsProjectBean()
            xlsProjectBean.title = data.getStringExtra("title")
            xlsProjectBean.content = data.getStringExtra("content")
            xlsProjectBean.checked = true
            adapter.addData(xlsProjectBean)
            recycler_view.scrollToPosition(adapter.data.size - 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        previewCall?.cancel()
        submitCall?.cancel()
    }

}
