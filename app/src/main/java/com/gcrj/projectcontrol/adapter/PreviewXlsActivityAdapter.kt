package com.gcrj.projectcontrol.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.bean.ActivityBean
import com.gcrj.projectcontrol.viewRelated.SimpleAdaptiveLayoutManager

/**
 * Created by zhangxin on 2018/9/18.
 */
class PreviewXlsActivityAdapter(private val list: List<ActivityBean>?) : BaseQuickAdapter<ActivityBean, BaseViewHolder>(R.layout.recycler_view_item_layout_preview_xls_activity, list) {

    var listener: ((checked: Boolean) -> Unit)? = null

    override fun convert(helper: BaseViewHolder, item: ActivityBean) {
        helper.setText(R.id.tv, "↘↘${item.name}")

        val recyclerView = helper.getView<RecyclerView>(R.id.recycler_view)
        val adapter = if (recyclerView.adapter == null) {
            val adapter = PreviewXlsActivityRelatedAdapter(item.activityRelated)
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener { _, _, position ->
                val bean = adapter.data[position]
                bean.expanded = !bean.expanded
                adapter.notifyItemChanged(position)
            }
            recyclerView.layoutManager = SimpleAdaptiveLayoutManager(mContext)
            adapter
        } else {
            val adapter = recyclerView.adapter as PreviewXlsActivityRelatedAdapter
            adapter.setNewData(item.activityRelated)
            adapter
        }

        if (item.expanded) {
            recyclerView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.GONE
        }

        val cb = helper.getView<CheckBox>(R.id.cb)
        cb.isChecked = item.checked
        val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            item.checked = isChecked
            item.activityRelated?.forEach { activityRelated ->
                activityRelated.checked = isChecked
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
    }

    private fun callListener(isChecked: Boolean) {
        if ((isChecked && list!!.count { it.checked } == 1)
                || (!isChecked && list!!.count { it.checked } == 0)) {
            listener?.invoke(isChecked)
        }
    }

}