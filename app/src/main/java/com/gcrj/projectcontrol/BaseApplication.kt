package com.gcrj.projectcontrol

import android.app.Application
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.facebook.stetho.Stetho
import com.gcrj.projectcontrol.bean.LoginBean
import com.gcrj.projectcontrol.util.Constant
import com.gcrj.projectcontrol.util.SharedPreferenceUtils
import com.google.gson.Gson
import com.google.gson.JsonParseException
import org.greenrobot.eventbus.EventBus


class BaseApplication : Application() {

    companion object {
        @JvmStatic
        lateinit var application: BaseApplication
            private set
        @JvmStatic
        var USER_INFO: LoginBean? = null
            set(value) {
                SharedPreferenceUtils.setString(Constant.USER_INFO_KEY, Gson().toJson(value))
                field = value
            }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        EventBus.builder().addIndex(MyEventBusIndex()).installDefaultEventBus()
        JPushInterface.init(this)

        val userInfoStr = SharedPreferenceUtils.getString(this, Constant.USER_INFO_KEY)
        if (!TextUtils.isEmpty(userInfoStr)) {
            try {
                USER_INFO = Gson().fromJson(userInfoStr, LoginBean::class.java)
            } catch (e: JsonParseException) {
            }
        }
    }

}
