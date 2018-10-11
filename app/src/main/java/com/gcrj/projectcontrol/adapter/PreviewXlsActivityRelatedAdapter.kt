package com.gcrj.projectcontrol.adapter

import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.bean.ActivityRelatedBean

/**
 * Created by zhangxin on 2018/9/18.
 */
class PreviewXlsActivityRelatedAdapter(private val list: List<ActivityRelatedBean>?) : BaseQuickAdapter<ActivityRelatedBean, BaseViewHolder>(R.layout.recycler_view_item_layout_preview_xls_activity_related, list) {

    var listener: ((checked: Boolean) -> Unit)? = null

    override fun convert(helper: BaseViewHolder, item: ActivityRelatedBean) {
        helper.setText(R.id.tv, "↘↘↘${item.name}")
        val cb = helper.getView<CheckBox>(R.id.cb)
        cb.tag = item.checked
        cb.setOnCheckedChangeListener { _, isChecked ->
            item.checked = isChecked
            if ((isChecked && list!!.count { it.checked } == 1)
                    || (!isChecked && list!!.count { it.checked } == 0)) {
                listener?.invoke(isChecked)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        val cb = holder.getView<CheckBox>(R.id.cb)
        cb.isChecked = cb.tag as Boolean
    }

}