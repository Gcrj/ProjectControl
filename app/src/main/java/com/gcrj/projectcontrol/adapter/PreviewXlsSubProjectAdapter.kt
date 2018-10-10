package com.gcrj.projectcontrol.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
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
            adapter.setOnItemClickListener { _, _, position ->
                val bean = adapter.data[position]
                bean.expanded = !bean.expanded
                adapter.notifyItemChanged(position)
            }
            recyclerView.layoutManager = SimpleAdaptiveLayoutManager(mContext)
            adapter
        } else {
            val adapter = recyclerView.adapter as PreviewXlsActivityAdapter
            adapter.setNewData(item.activity)
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
    }

    private fun callListener(isChecked: Boolean) {
        if ((isChecked && list!!.count { it.checked } == 1)
                || (!isChecked && list!!.count { it.checked } == 0)) {
            listener?.invoke(isChecked)
        }
    }

}