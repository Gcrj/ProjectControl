package com.gcrj.projectcontrol.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.appcompat.app.AppCompatActivity
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.util.AppManager

/**
 * Created by zhangxin on 2018/5/30.
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    @JvmField
    protected val TAG = this::class.java.simpleName
    @JvmField
    protected var isResumed = false
    @JvmField
    protected var swipeFinishEnable = true
    private var downX = 0.toFloat()
    private var lastX = 0.toFloat()
    private var swipeEnableThisTime = true
    private var edgeSlop: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.get().addActivity(this)
        overridePendingTransition(R.anim.anim_in_from_right, R.anim.anim_out_to_left)
        if (intent.getStringExtra("actionbar_title") != null) {
            title = intent.getStringExtra("actionbar_title")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.get().removeActivity(this)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_in_from_left, R.anim.anim_out_to_right)
    }

    override fun onResume() {
        super.onResume()
        isResumed = true
    }

    override fun onPause() {
        super.onPause()
        isResumed = false
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!swipeFinishEnable) {
            return super.dispatchTouchEvent(ev)
        }

        if (edgeSlop == 0) {
            val vc = ViewConfiguration.get(this)
            edgeSlop = vc.scaledEdgeSlop
        }

        if (ev.action == MotionEvent.ACTION_DOWN) {
            downX = ev.x
            lastX = downX
            swipeEnableThisTime = downX < edgeSlop
        } else if (swipeEnableThisTime && ev.action == MotionEvent.ACTION_MOVE) {
            if (ev.x < lastX) {
                swipeEnableThisTime = false
            }

            lastX = ev.x
        } else if (swipeEnableThisTime && ev.action == MotionEvent.ACTION_UP) {
            if (ev.x - downX > 200) {
                finish()
                ev.action = MotionEvent.ACTION_CANCEL
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true
        }

        return super.onKeyUp(keyCode, event)
    }

}