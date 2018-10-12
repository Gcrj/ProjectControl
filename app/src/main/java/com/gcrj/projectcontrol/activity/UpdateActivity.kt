package com.gcrj.projectcontrol.activity

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.gcrj.projectcontrol.BuildConfig
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.bean.CheckUpdateBean
import com.gcrj.projectcontrol.service.UpdateService
import com.gcrj.projectcontrol.util.AppManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.util.Tool
import kotlinx.android.synthetic.main.activity_update.*
import java.io.File
import java.lang.ref.WeakReference

class UpdateActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val REQUEST_CODE_INSTALL = 1

        const val DOWNLOAD_NONE = -1
        const val DOWNLOAD_BEGIN = 0
        const val DOWNLOAD_PROGRESS = 1
        const val DOWNLOAD_FINISH = 2
        const val DOWNLOAD_ERROR = 3

        const val NOTIFICATION_ID = 3002
        const val CHANNEL_ID = "update_id"
        const val CHANNEL_NAME = "update"
        const val CHANNEL_DESC = "update_desc"

        class MyHandler(updateActivity: UpdateActivity) : Handler() {

            private val activityWeakReference: WeakReference<UpdateActivity> = WeakReference(updateActivity)

            override fun handleMessage(msg: Message) {
                val updateActivity = activityWeakReference.get()
                if (updateActivity == null || updateActivity.isDestroyed) {
                    return
                }

                when (msg.what) {
                    DOWNLOAD_BEGIN -> updateActivity.downloadBegin(msg.arg1)
                    DOWNLOAD_PROGRESS -> updateActivity.updateProgress(msg.arg1, msg.arg2)
                    DOWNLOAD_FINISH -> updateActivity.downloadFinish()
                    DOWNLOAD_ERROR -> updateActivity.downloadError()
                }
            }
        }
    }

    private var currentState = DOWNLOAD_NONE

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private lateinit var file: File

    private val bean by lazy {
        intent.getParcelableExtra("bean") as CheckUpdateBean
    }
    private val url: String by lazy {
        bean.url ?: ""
    }
    private val message: String by lazy {
        bean.message ?: ""
    }
    private val versionName: String by lazy {
        bean.version_name ?: ""
    }
    private val forceUpdate by lazy {
        bean.force_update ?: false
    }

    private lateinit var updateService: UpdateService
    private var isBind = false
    private val serviceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                isBind = true
                updateService = (service as UpdateService.MyBinder).getService()
                updateService.setInfo(MyHandler(this@UpdateActivity), file, url)
                updateService.beginDownload()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBind = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_update)
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(message) || TextUtils.isEmpty(versionName)) {
            ToastUtils.showToast("缺少更新信息")
            finish()
            return
        }

        val dir =
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    getExternalFilesDir(null)
                } else {
                    cacheDir//多存储卡设备getExternalFilesDir不一定会返回哪张存储卡的externalFilesDir,导致FileProvider判断有误，相比重写FileProvider，还是用cacheDir吧
                }

        if (dir == null) {
            ToastUtils.showToast("存储空间不可用")
            finish()
            return
        }

        val apkDir = File(dir, "apk")
        apkDir.deleteRecursively()
        if (!apkDir.exists()) {
            if (!apkDir.mkdirs()) {
                ToastUtils.showToast("存储空间不可用")
                finish()
                return
            }
        }

        file = File(apkDir, resources.getString(R.string.app_name) + if (TextUtils.isEmpty(versionName)) ".apk" else "_$versionName.apk")
        if (file.exists()) {
            if (!file.delete()) {
                ToastUtils.showToast("IO异常")
                finish()
                return
            }
        }

        if (forceUpdate) {
            tv_title.text = "重要更新"
            tv_cancel.text = "关闭"
        } else {
            tv_title.text = "已有新版"
            tv_cancel.text = "取消"
        }

        tv_message.text = message.replace("\\n","\n")
        fl_parent.setOnClickListener(this)
        tv_update.setOnClickListener(this)
        tv_cancel.setOnClickListener(this)
        tv_install_or_failed.setOnClickListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        onClick(tv_install_or_failed)
    }

    private fun downloadApp() {
        currentState = DOWNLOAD_BEGIN
        tv_title.text = "正在下载"
        ll_btn.visibility = View.GONE
        ll_progress.visibility = View.VISIBLE
        if (isBind) {
            updateService.beginDownload()
        } else {
            bindService(Intent(this, UpdateService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun downloadBegin(totalSize: Int) {
        tv_download_size.text = "0KB"
        tv_total_size.text = "/${Tool.getFormatSize(totalSize.toDouble())}"
    }

    private fun updateProgress(progress: Int, downloadSize: Int) {
        currentState = DOWNLOAD_PROGRESS
        pb.progress = progress
        tv_progress.text = "$progress%"
        tv_download_size.text = Tool.getFormatSize(downloadSize.toDouble())

        val intent = Intent(this, UpdateActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("互动百科").setContentText("正在下载").setTicker("正在下载").setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true).setAutoCancel(false)
                .setProgress(100, progress, false)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun downloadFinish() {
        currentState = DOWNLOAD_FINISH
        tv_title.text = "下载完成"
        pb.progress = 100
        tv_progress.text = "100%"
        tv_download_size.text = tv_total_size.text.toString().substring(1)
        tv_install_or_failed.text = "去安装"
        tv_install_or_failed.visibility = View.VISIBLE
        val intent = Intent(this, UpdateActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("互动百科").setContentText("下载完成").setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(false).setAutoCancel(false)
        notificationManager.notify(NOTIFICATION_ID, builder.build())

        onClick(tv_install_or_failed)
    }

    private fun downloadError() {
        currentState = DOWNLOAD_ERROR
        tv_title.text = "下载失败"
        tv_install_or_failed.text = "重试"
        tv_install_or_failed.visibility = View.VISIBLE
        val intent = Intent(this, UpdateActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("互动百科").setContentText("下载失败").setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true).setAutoCancel(false)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onBackPressed() {
        onClick(fl_parent)
    }

    override fun onClick(v: View) {
        when (v) {
            tv_update -> downloadApp()
            fl_parent -> {
                if (!forceUpdate && (currentState == DOWNLOAD_NONE || currentState == DOWNLOAD_ERROR)) {
                    finish()
                }
            }
            tv_cancel -> {
                if (forceUpdate) {
                    AppManager.get().finishAllActivity()
                } else {
                    finish()
                }
            }
            tv_install_or_failed -> {
                if (currentState == DOWNLOAD_FINISH) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (packageManager.canRequestPackageInstalls()) {
                            openInstallActivity()
                        } else {
                            ToastUtils.showToast("请打开应用安装权限")
                            val install = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                            if (packageManager.queryIntentActivities(install, 0).size > 0) {
                                startActivityForResult(install, REQUEST_CODE_INSTALL)
                            }
                        }
                    } else {
                        openInstallActivity()
                    }
                } else if (currentState == DOWNLOAD_ERROR) {
                    v.visibility = View.GONE
                    downloadApp()
                }
            }
        }
    }

    private fun openInstallActivity() {
        if (!file.exists()) {
            ToastUtils.showToast("安装文件不存在")
            finish()
            return
        }

        val uri: Uri
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file)
        } else {
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        }

        val install = Intent(Intent.ACTION_VIEW)
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        install.setDataAndType(uri, "application/vnd.android.package-archive")
        if (packageManager.queryIntentActivities(install, 0).size > 0) {
            startActivity(install)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INSTALL && resultCode == Activity.RESULT_OK && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (packageManager.canRequestPackageInstalls()) {
                openInstallActivity()
            } else {
                ToastUtils.showToast("未打开应用安装权限")
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        if (isBind) {
            unbindService(serviceConnection)
        }

        super.onDestroy()
    }

}
