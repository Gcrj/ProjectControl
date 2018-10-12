package com.gcrj.projectcontrol.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.bean.ProjectBean

/**
 * Created by zhangxin on 2018/9/18.
 */
class ProjectAdapter : BaseQuickAdapter<ProjectBean, BaseViewHolder>(R.layout.recycler_view_item_layout_project) {

    override fun convert(helper: BaseViewHolder, item: ProjectBean?) {
        helper.setText(R.id.tv_project, item?.name)
//        helper.setText(R.id.tv_user, item?.create_user?.username)
    }

}