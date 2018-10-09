package com.gcrj.projectcontrol.bean

class ActivityBean : CheckableAndExpandableBean(), Cloneable {

    var id: Int? = null
    var sub_project_id: Int? = null
    var name: String? = null
    var progress: Int? = null

    var activityRelated: List<ActivityRelatedBean>? = null

    public override fun clone(): ActivityBean {
        val bean = super.clone() as ActivityBean
        bean.activityRelated = activityRelated?.map {
            it.clone()
        }

        return bean
    }
}