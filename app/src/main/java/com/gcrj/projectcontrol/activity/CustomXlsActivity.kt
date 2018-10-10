package com.gcrj.projectcontrol.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.base.BaseActivity
import com.gcrj.projectcontrol.util.ToastUtils
import kotlinx.android.synthetic.main.activity_custom_xls.*

class CustomXlsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_xls)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn.setOnClickListener {
            val title = et_title.text.toString().trim()
            if (title == "") {
                ToastUtils.showToast(et_title.hint.toString())
                return@setOnClickListener
            }

            val content = et_content.text.toString().trim()
            if (content == "") {
                ToastUtils.showToast(et_content.hint.toString())
                return@setOnClickListener
            }

            val intent = Intent()
            intent.putExtra("title", title)
            intent.putExtra("content", content)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}
