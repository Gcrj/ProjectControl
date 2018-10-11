package com.gcrj.projectcontrol.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.bean.SubProjectBean
import com.gcrj.projectcontrol.viewRelated.SimpleAdaptiveLayoutManager

/**
 * Created by zhangxin on 2018/9/18.
 */
class PreviewXlsSubProjectAdapter(private val list: List<SubProjectBean>?) : BaseQuickAdapter<SubProjectBean, BaseViewHolder>(R.layout.recycler_view_item_layout_preview_xls_sub_project, list) {

    var listener: ((checked: Boolean) -> Unit)? = null

    override fun convert(helper: BaseViewHolder, item: SubProjectBean) {
        helper.setText(R.id.tv, "↘${item.name}")
        helper.setText(R.id.tv_deadline, "${item.deadline}")

        val recyclerView = helper.getView<RecyclerView>(R.id.recycler_view)
        val adapter = if (recyclerView.adapter == null) {
            val adapter = PreviewXlsActivityAdapter(item.activity)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = SimpleAdaptiveLayoutManager(mContext)
            adapter
        } else {
            val adapter = recyclerView.adapter as PreviewXlsActivityAdapter
            adapter.setNewData(item.activity)
            adapter
        }

        recyclerView.visibility = if (item.expanded) View.VISIBLE else View.GONE

        val cb = helper.getView<CheckBox>(R.id.cb)
        cb.tag = item.checked
        val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            item.checked = isChecked
            item.activity?.forEach { activity ->
                activity.checked = isChecked
                activity.activityRelated?.forEach { activityRelated ->
                    activityRelated.checked = isChecked
                }
            }
            adapter.notifyDataSetChanged()

            callListener(isChecked)
        }
        cb.setOnCheckedChangeListener(onCheckedChangeListener)

        adapter.listener = {
            cb.setOnCheckedChangeListener(null)
            cb.isChecked = it
            item.checked = it
            cb.setOnCheckedChangeListener(onCheckedChangeListener)

            callListener(it)
        }

        val ivArrow = helper.getView<ImageView>(R.id.iv_arrow)
        ivArrow.rotation = if (item.expanded) 90F else 0F
        ivArrow.setOnClickListener {
            item.expanded = !item.expanded
            recyclerView.visibility = if (item.expanded) View.VISIBLE else View.GONE
            ivArrow.animate().rotation(if (item.expanded) 90F else 0F).start()
        }
    }

    private fun callListener(isChecked: Boolean) {
        if ((isChecked && list!!.count { it.checked } == 1)
                || (!isChecked && list!!.count { it.checked } == 0)) {
            listener?.invoke(isChecked)
        }
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        val cb = holder.getView<CheckBox>(R.id.cb)
        cb.isChecked = cb.tag as Boolean
    }

}