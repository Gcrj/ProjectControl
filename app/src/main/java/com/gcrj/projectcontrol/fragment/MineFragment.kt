package com.gcrj.projectcontrol.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.gcrj.projectcontrol.BaseApplication
import com.gcrj.projectcontrol.BuildConfig
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.activity.SettingActivity
import com.gcrj.projectcontrol.base.BaseFragment
import com.gcrj.projectcontrol.bean.ResponseBean
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.util.startActivity
import com.gcrj.projectcontrol.view.ProgressDialog
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_mine.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URLDecoder

class MineFragment : BaseFragment(), View.OnClickListener {

    private var call: Call<ResponseBody>? = null

    override fun inflateView() = R.layout.fragment_mine

    private val dialog by lazy {
        val dialog = ProgressDialog(context)
        dialog.setMessage("正在获取周报")
        dialog
    }

    override fun init(savedInstanceState: Bundle?) {
        tv_name.text = BaseApplication.USER_INFO?.username
        tv_preview_xls.setOnClickListener(this)
        tv_my_setting.setOnClickListener(this)
    }

    override fun visibleToUser() {
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.mine)
    }

    override fun onClick(v: View?) {
        when (v) {
            tv_preview_xls -> getXls()
            tv_my_setting -> startActivity<SettingActivity>()
        }
    }

    private fun getXls() {
        dialog.show()
        call = RetrofitManager.apiService.getXls()
        call?.enqueue(callback)
    }

    private val callback by lazy {
        object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
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

                        val file = File(context!!.cacheDir, name)
                        fis = response.body()!!.byteStream()
                        fos = FileOutputStream(file)
                        val b = ByteArray(1024)
                        var read = fis.read(b)
                        while (read != -1) {
                            fos.write(b, 0, read)
                            read = fis.read(b)
                        }

                        fos.flush()
                        activity?.runOnUiThread {
                            dialog.dismiss()
                            val intent = Intent(Intent.ACTION_VIEW)
                            val uri: Uri
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                uri = Uri.fromFile(file)
                            } else {
                                uri = FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", file)
                            }

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                            if (context!!.packageManager.queryIntentActivities(intent, 0).size > 0) {
                                startActivity(intent)
                            } else {
                                ToastUtils.showToast("未发现能打开此文件的应用")
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        activity?.runOnUiThread {
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
                ToastUtils.showToast("网络异常")
                dialog.dismiss()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        call?.cancel()
    }

}