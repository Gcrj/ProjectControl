package com.gcrj.projectcontrol.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.activity.UpdateActivity
import com.gcrj.projectcontrol.activity.UpdateActivity.Companion.CHANNEL_DESC
import com.gcrj.projectcontrol.activity.UpdateActivity.Companion.CHANNEL_ID
import com.gcrj.projectcontrol.activity.UpdateActivity.Companion.CHANNEL_NAME
import com.gcrj.projectcontrol.activity.UpdateActivity.Companion.NOTIFICATION_ID
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class UpdateService : Service() {

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    inner class MyBinder : Binder() {
        fun getService() = this@UpdateService
    }

    override fun onBind(intent: Intent?): IBinder {
        return MyBinder()
    }

    private lateinit var handler: UpdateActivity.Companion.MyHandler
    private lateinit var file: File
    private lateinit var url: String
    private var isCancel = false

    fun setInfo(handler: UpdateActivity.Companion.MyHandler, file: File, url: String) {
        this.handler = handler
        this.file = file
        this.url = url
    }

    fun beginDownload() {
        notificationManager.notify(UpdateActivity.NOTIFICATION_ID, getDefaultNotification())

        object : Thread() {
            override fun run() {
                var inputStream: InputStream? = null
                var fos: FileOutputStream? = null
                try {
                    val conn = URL(url).openConnection() as HttpURLConnection
                    conn.readTimeout = 10_000
                    conn.connectTimeout = 10_000
                    if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                        handler.sendEmptyMessage(UpdateActivity.DOWNLOAD_ERROR)
                        return
                    }

                    inputStream = conn.inputStream
                    var downloadSize = 0
                    val totalSize = conn.contentLength
                    if (totalSize <= 0) {
                        handler.sendEmptyMessage(UpdateActivity.DOWNLOAD_ERROR)
                        return
                    }

                    handler.sendMessage(handler.obtainMessage(UpdateActivity.DOWNLOAD_BEGIN, totalSize, 0))
                    fos = FileOutputStream(file)
                    val byteArray = ByteArray(1024)
                    var len = inputStream.read(byteArray)
                    var progress = 0
                    while (len != -1 && !isCancel) {
                        fos.write(byteArray, 0, len)
                        downloadSize += len

                        val newProgress = (downloadSize * 100F / totalSize).toInt()
                        if (progress != newProgress) {
                            progress = newProgress
                            handler.sendMessage(handler.obtainMessage(UpdateActivity.DOWNLOAD_PROGRESS, progress, downloadSize))
                        }

                        len = inputStream.read(byteArray)
                    }

                    if (isCancel) {
                        file.delete()
                    } else {
                        fos.flush()
                        handler.sendEmptyMessage(UpdateActivity.DOWNLOAD_FINISH)
                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                    handler.sendEmptyMessage(UpdateActivity.DOWNLOAD_ERROR)
                } catch (e: IOException) {
                    e.printStackTrace()
                    handler.sendEmptyMessage(UpdateActivity.DOWNLOAD_ERROR)
                } finally {
                    inputStream?.close()
                    fos?.close()
                }
            }
        }.start()
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID, getDefaultNotification())
    }

    private fun getDefaultNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("互动百科").setContentText("正在下载").setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("正在下载").setOngoing(true).setAutoCancel(false)
            .setProgress(100, 0, false).build()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(id: String = CHANNEL_ID, name: String = CHANNEL_NAME, desc: String = CHANNEL_DESC) {
        if (notificationManager.getNotificationChannel(id) != null) return
        val notificationChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(false)
        notificationChannel.lightColor = Color.YELLOW
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationChannel.setShowBadge(true)
        notificationChannel.setBypassDnd(true)
        notificationChannel.description = desc
        notificationManager.createNotificationChannel(notificationChannel)
    }

    override fun onDestroy() {
        super.onDestroy()
        isCancel = true
    }

}