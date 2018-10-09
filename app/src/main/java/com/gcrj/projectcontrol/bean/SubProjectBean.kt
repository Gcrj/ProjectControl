package com.gcrj.projectcontrol.bean

class SubProjectBean : CheckableAndExpandableBean(), Cloneable {

    var id: Int? = null
    var project_id: Int? = null
    var name: String? = null
    var progress: Int? = null

    var activity: List<ActivityBean>? = null

    public override fun clone(): SubProjectBean {
        val bean = super.clone() as SubProjectBean
        bean.activity = activity?.map {
            it.clone()
        }

        return bean
    }

}