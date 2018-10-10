package com.gcrj.projectcontrol.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by zhangxin on 2018/10/10.
 */
class XlsProjectBean : ProjectBean(), MultiItemEntity {

    companion object {
        const val TYPE_PROJECT = 1
        const val TYPE_CUSTOM = 2

        fun parseXlsProjectBean(projectBean: ProjectBean): XlsProjectBean {
            val xlsProjectBean = XlsProjectBean()
            xlsProjectBean.id = projectBean.id
            xlsProjectBean.name = projectBean.name
            xlsProjectBean.create_user = projectBean.create_user
            xlsProjectBean.subProject = projectBean.subProject
            xlsProjectBean.checked = projectBean.checked
            xlsProjectBean.expanded = projectBean.expanded
            xlsProjectBean.type = TYPE_PROJECT
            return xlsProjectBean
        }
    }

    var title: String? = null
    var content: String? = null
    var type = TYPE_CUSTOM

    override fun getItemType() = type

}