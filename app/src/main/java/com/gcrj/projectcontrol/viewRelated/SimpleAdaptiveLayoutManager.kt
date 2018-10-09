package com.gcrj.projectcontrol.viewRelated

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by zhangxin on 2018/10/9.
 */
class SimpleAdaptiveLayoutManager : LinearLayoutManager {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        super.onMeasure(recycler, state, widthSpec, heightSpec)
        if (childCount > 0) {
            val view = getChildAt(0)!!
            measureChildWithMargins(view, 0, 0)
            setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), getDecoratedMeasuredHeight(view) * itemCount)
        }
    }

}