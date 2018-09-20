package com.gcrj.projectcontrol.adapter

import android.os.Build
import android.widget.SeekBar
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.bean.ActivityRelatedBean

/**
 * Created by zhangxin on 2018/9/18.
 */
class ActivityRelatedAdapter : BaseQuickAdapter<ActivityRelatedBean, BaseViewHolder>(R.layout.recycler_view_item_layout_activity_related) {

    override fun convert(helper: BaseViewHolder, item: ActivityRelatedBean?) {
        helper.setText(R.id.tv_activity_related, item?.name)
        helper.setText(R.id.tv_progress, "当前进度 ${item?.progress}%")
        val seekBar = helper.getView<SeekBar>(R.id.seek_bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(item?.progress ?: 0, true)
        } else {
            seekBar.progress = item?.progress ?: 0
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                item?.progress = progress
                helper.setText(R.id.tv_progress, "当前进度 $progress%")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

}