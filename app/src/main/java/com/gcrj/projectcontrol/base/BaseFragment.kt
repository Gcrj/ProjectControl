package com.gcrj.projectcontrol.base

import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat

/**
 * Created by zhangxin on 2017/7/19.
 */

abstract class BaseFragment : androidx.fragment.app.Fragment() {

    @JvmField
    protected val TAG: String = javaClass.simpleName
    @JvmField
    protected var rootView: View? = null
    protected var isReuse: Boolean = false
        private set

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(inflateView(), container, false)
            isReuse = false
        } else {
            isReuse = true
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(savedInstanceState)
        if (savedInstanceState == null) {
            fitSystemWindows(true)
        }

        if (!isHidden) {
            visibleToUser()
        }
    }

    @LayoutRes
    abstract fun inflateView(): Int

    open fun fitSystemWindowsView(): View? = null

    abstract fun init(savedInstanceState: Bundle?)

    protected fun <T : View> findViewById(@IdRes id: Int): T? = rootView?.findViewById(id)

    override fun onHiddenChanged(hidden: Boolean) {
        fitSystemWindows(!hidden)

        if (!hidden) {
            visibleToUser()
        }
    }

    @CallSuper
    open fun fitSystemWindows(fitSystemWindows: Boolean) {
        val view = fitSystemWindowsView() ?: return
        view.fitsSystemWindows = fitSystemWindows
        ViewCompat.requestApplyInsets(view)
    }

    open fun visibleToUser() {

    }

    open fun onKeyUp(keyCode: Int, event: KeyEvent) = false

    open fun dispatchTouchEvent(ev: MotionEvent) {}

}
