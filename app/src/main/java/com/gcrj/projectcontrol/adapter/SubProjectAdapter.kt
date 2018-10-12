package com.gcrj.projectcontrol.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.bean.SubProjectBean

/**
 * Created by zhangxin on 2018/9/18.
 */
class SubProjectAdapter : BaseQuickAdapter<SubProjectBean, BaseViewHolder>(R.layout.recycler_view_item_layout_sub_project) {

    override fun convert(helper: BaseViewHolder, item: SubProjectBean?) {
        helper.setText(R.id.tv_project, item?.name)
        if (item?.completionTime != null) {
            helper.setText(R.id.tv_progress, "已于${item.completionTime}完成")
        } else {
            helper.setText(R.id.tv_progress, "当前进度 ${item?.progress}%")
        }
    }

}