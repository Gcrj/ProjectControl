package com.gcrj.projectcontrol.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.bean.ActivityBean

/**
 * Created by zhangxin on 2018/9/18.
 */
class ActivityAdapter : BaseQuickAdapter<ActivityBean, BaseViewHolder>(R.layout.recycler_view_item_layout_activity) {

    override fun convert(helper: BaseViewHolder, item: ActivityBean?) {
        helper.setText(R.id.tv_project, item?.name)
        helper.setText(R.id.tv_progress, "当前进度 ${item?.progress}%")
    }

}