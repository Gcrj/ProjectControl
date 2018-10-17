package com.gcrj.projectcontrol.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.bean.SubProjectBean

/**
 * Created by zhangxin on 2018/9/18.
 */
class SubProjectAdapter : BaseQuickAdapter<SubProjectBean, BaseViewHolder>(R.layout.recycler_view_item_layout_sub_project) {

    override fun convert(helper: BaseViewHolder, item: SubProjectBean) {
        val versionName = if (item.versionName == null) "" else " ${item.versionName}"
        helper.setText(R.id.tv_project, "${item.name}(${item.projectName}$versionName)")
        val tvInfo = helper.getView<TextView>(R.id.tv_info)
        tvInfo.text = "计划${item.deadline}完成\n"
        if (item.completionTime != null) {
            tvInfo.append("实际${item.completionTime}完成")
        } else {
            tvInfo.append("当前进度${item.progress}%")
        }
    }

}