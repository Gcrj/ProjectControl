package com.gcrj.projectcontrol.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.bean.ProjectBean
import com.gcrj.projectcontrol.viewRelated.SimpleAdaptiveLayoutManager

/**
 * Created by zhangxin on 2018/9/18.
 */
class PreviewXlsProjectAdapter : BaseQuickAdapter<ProjectBean, BaseViewHolder>(R.layout.recycler_view_item_layout_preview_xls_project) {

    override fun convert(helper: BaseViewHolder, item: ProjectBean) {
        helper.setText(R.id.tv, item.name)

        val recyclerView = helper.getView<RecyclerView>(R.id.recycler_view)
        val adapter = if (recyclerView.adapter == null) {
            val adapter = PreviewXlsSubProjectAdapter(item.subProject)
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener { _, _, position ->
                val bean = adapter.data[position]
                bean.expanded = !bean.expanded
                adapter.notifyItemChanged(position)
            }
            recyclerView.layoutManager = SimpleAdaptiveLayoutManager(mContext)
            adapter
        } else {
            val adapter = recyclerView.adapter as PreviewXlsSubProjectAdapter
            adapter.setNewData(item.subProject)
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
            item.subProject?.forEach { subProject ->
                subProject.checked = isChecked
                subProject.activity?.forEach { activity ->
                    activity.checked = isChecked
                    activity.activityRelated?.forEach { activityRelated ->
                        activityRelated.checked = isChecked
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
        cb.setOnCheckedChangeListener(onCheckedChangeListener)

        adapter.listener = {
            cb.setOnCheckedChangeListener(null)
            cb.isChecked = it
            item.checked = it
            cb.setOnCheckedChangeListener(onCheckedChangeListener)
        }
    }

}