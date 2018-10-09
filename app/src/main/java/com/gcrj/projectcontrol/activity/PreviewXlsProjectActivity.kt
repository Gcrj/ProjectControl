package com.gcrj.projectcontrol.activity

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
import com.gcrj.projectcontrol.http.NothingResponseCallback
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.ToastUtils
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

class PreviewXlsProjectActivity : BaseActivity(), LoadingLayout.OnRetryListener {

    private var preciewCall: Call<ResponseBody>? = null
    private var submitCall: Call<ResponseBean<Nothing>>? = null

    private val dialog by lazy {
        val dialog = ProgressDialog(this)
        dialog
    }

    private var data: List<ProjectBean>? = null
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

                this@PreviewXlsProjectActivity.data = data
                recycler_view.layoutManager = LinearLayoutManager(this@PreviewXlsProjectActivity)
                recycler_view.adapter = adapter
                val divider = RecycleViewDivider(this@PreviewXlsProjectActivity, LinearLayoutManager.HORIZONTAL)
                recycler_view.addItemDecoration(divider)
                adapter.setOnItemClickListener { _, _, position ->
                    val bean = adapter.data[position]
                    bean.expanded = !bean.expanded
                    adapter.notifyItemChanged(position)
                }
                adapter.setNewData(data)
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
        menuInflater.inflate(R.menu.menu_preview_xls_project, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) = data != null

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_preview -> {
                val filterData = data!!.asSequence().map { it.clone() }.filter {
                    it.checked
                }.toList()

                if (filterData.isEmpty()) {
                    ToastUtils.showToast("选中内容为空")
                    return true
                }

                dialog.setMessage("正在获取周报")
                dialog.show()
                filterData.forEach { project ->
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
                preciewCall = RetrofitManager.apiService.previewXls(RequestBody.create(okhttp3.MediaType.parse("application/json"), Gson().toJson(filterData)))
                preciewCall?.enqueue(previewCallback)
                return true
            }
            R.id.menu_submit -> {
                val filterData = data!!.asSequence().map { it.clone() }.filter {
                    it.checked
                }.toList()

                if (filterData.isEmpty()) {
                    ToastUtils.showToast("选中内容为空")
                    return true
                }

                dialog.setMessage("正在提交周报")
                dialog.show()
                filterData.forEach { project ->
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
                submitCall = RetrofitManager.apiService.submitXls(RequestBody.create(okhttp3.MediaType.parse("application/json"), Gson().toJson(filterData)))
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

                if (response.body()?.contentType()?.toString() == "text/html;charset=utf-8") {
                    try {
                        val responseBean = Gson().fromJson<ResponseBean<Nothing>>(response.body()!!.string(), object : TypeToken<ResponseBean<Nothing>>() {}.type)
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
                        fis = response.body()!!.byteStream()
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
                            val uri: Uri
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                uri = Uri.fromFile(file)
                            } else {
                                uri = FileProvider.getUriForFile(this@PreviewXlsProjectActivity, BuildConfig.APPLICATION_ID + ".provider", file)
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

    override fun onDestroy() {
        super.onDestroy()
        preciewCall?.cancel()
    }

}
