package com.gcrj.projectcontrol.bean

/**
 * Created by zhangxin on 2018/6/5.
 */
class ResponseBean<T> {

    var status: Int = 0
    var msg: String? = null
    var result: T? = null

}
