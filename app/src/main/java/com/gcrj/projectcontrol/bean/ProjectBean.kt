package com.gcrj.projectcontrol.bean

open class ProjectBean : CheckableAndExpandableBean(), Cloneable {

    var id: Int? = null
    var name: String? = null
    var create_user: UserBean? = null

    var subProject: List<SubProjectBean>? = null

    public override fun clone(): ProjectBean {
        val bean = super.clone() as ProjectBean
        bean.subProject = subProject?.map {
            it.clone()
        }

        return bean
    }

}