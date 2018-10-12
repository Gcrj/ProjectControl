package com.gcrj.projectcontrol.util

import android.app.Activity
import java.util.*

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
class AppManager private constructor() {

    companion object {

        private val activityStack by lazy {
            Stack<Activity>()
        }
        private var instance: AppManager? = null

        fun get(): AppManager {
            if (instance == null) {
                instance = AppManager()
            }

            return instance!!
        }
    }

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    internal fun currentActivity(): Activity {
        return activityStack.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        val activity = activityStack.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity) {
        activityStack.remove(activity)
        activity.finish()
    }

    fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        activityStack
                .filter { it.javaClass == cls }
                .forEach { finishActivity(it) }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        var i = 0
        val size = activityStack.size
        while (i < size) {
            if (null != activityStack[i]) {
                activityStack[i].finish()
            }

            i++
        }

        activityStack.clear()
    }

}