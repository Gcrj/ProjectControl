package com.gcrj.projectcontrol.bean

class ActivityRelatedBean : CheckableAndExpandableBean(), Cloneable {

    var id: Int? = null
    var activity_id: Int? = null
    var name: String? = null
    var progress: Int? = null

    public override fun clone() = super.clone() as ActivityRelatedBean

}