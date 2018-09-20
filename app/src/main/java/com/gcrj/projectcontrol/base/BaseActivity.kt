package com.gcrj.projectcontrol.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.gcrj.projectcontrol.R

/**
 * Created by zhangxin on 2018/5/30.
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    @JvmField
    protected val TAG = javaClass.simpleName
    @JvmField
    protected var isResumed = false
    @JvmField
    protected var swipeFinishEnable = true
    private var downX: Float = 0.toFloat()
    private var lastX: Float = 0.toFloat()
    private var swipeEnableThisTime = true
    private var edgeSlop: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_in_from_right, R.anim.anim_out_to_left)
        if (intent.getStringExtra("actionbar_title") != null) {
            title = intent.getStringExtra("actionbar_title")
        }
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
            val moveDistanceX = ev.x - downX
            if (moveDistanceX > 200) {
                finish()
                val now = SystemClock.uptimeMillis()
                super.dispatchTouchEvent(MotionEvent.obtain(now, now, MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0))
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

}