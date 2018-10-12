package com.gcrj.projectcontrol.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import java.util.*

class FlowGroupView : ViewGroup {

    private val linesView = mutableListOf<MutableList<View>>()
    //每行的最大高度
    private val linesHeight = LinkedList<Int>()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun generateDefaultLayoutParams(): LayoutParams {
        return ViewGroup.MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        linesView.clear()
        linesHeight.clear()

        val specWidthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val specHeightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val specWidthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val specHeightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        var width = 0
        var height = paddingTop + paddingBottom
        //当前行宽
        var lineWidth = paddingLeft + paddingRight
        //当前行高
        var lineHeight = 0
        var lineView = mutableListOf<View>()

        (0 until childCount).map {
            getChildAt(it)
        }.forEach { child ->
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams
            val cWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val cHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin

            if (lineWidth + cWidth <= specWidthSize) {//不需要换行
                lineWidth += cWidth
                if (cHeight > lineHeight) {//当前child高比行高大，替换掉
                    lineHeight = cHeight

                    if (linesHeight.isNotEmpty()) {
                        height -= linesHeight.removeLast()
                    }

                    linesHeight.add(lineHeight)
                    height += lineHeight
                }

                lineView.add(child)

                if (lineWidth > width) {
                    width = lineWidth
                }

                if (linesView.isEmpty()) {
                    linesView.add(lineView)
                }
            } else {//换行
                lineWidth = paddingLeft + paddingRight + cWidth
                if (lineWidth > width) {
                    width = lineWidth
                }

                height += cHeight
                linesHeight.add(cHeight)

                lineView = mutableListOf()
                lineView.add(child)
                linesView.add(lineView)
            }
        }

        setMeasuredDimension(if (specWidthMode == View.MeasureSpec.EXACTLY) specWidthSize else width, if (specHeightMode == View.MeasureSpec.EXACTLY) specHeightSize else height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = paddingTop
        (0 until linesView.size).forEach { row ->
            val lineViews = linesView[row]
            var left = paddingLeft
            val lineHeight = linesHeight[row]

            (0 until lineViews.size).forEach {
                val child = lineViews[it]
                val lp = child.layoutParams as ViewGroup.MarginLayoutParams
                val cl = left + lp.leftMargin
                val ct = top + lp.topMargin + (lineHeight - (child.measuredHeight + lp.topMargin + lp.bottomMargin)) / 2
                val cr = cl + child.measuredWidth
                val cb = ct + child.measuredHeight
                child.layout(cl, ct, cr, cb)

                left = cr + lp.rightMargin
            }

            top += lineHeight
        }

    }

}
