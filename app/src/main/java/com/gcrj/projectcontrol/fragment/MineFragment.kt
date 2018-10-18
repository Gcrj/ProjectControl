package com.gcrj.projectcontrol.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gcrj.projectcontrol.BaseApplication
import com.gcrj.projectcontrol.R
import com.gcrj.projectcontrol.activity.PreviewXlsProjectActivity
import com.gcrj.projectcontrol.activity.SettingActivity
import com.gcrj.projectcontrol.base.BaseFragment
import com.gcrj.projectcontrol.http.ResponseCallback
import com.gcrj.projectcontrol.http.RetrofitManager
import com.gcrj.projectcontrol.util.ToastUtils
import com.gcrj.projectcontrol.util.startActivity
import kotlinx.android.synthetic.main.fragment_mine.*
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream


class MineFragment : BaseFragment(), View.OnClickListener {

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1
        private const val REQUEST_CODE_CROP_IMAGE = 2
    }

    override fun inflateView() = R.layout.fragment_mine

    override fun init(savedInstanceState: Bundle?) {
        tv_name.text = BaseApplication.USER_INFO?.username
        if (BaseApplication.USER_INFO?.avator != null) {
            Glide.with(context!!).load(BaseApplication.USER_INFO?.avator).into(iv_avator)
        }

        iv_avator.setOnClickListener(this)
        tv_preview_and_submit_xls.setOnClickListener(this)
        tv_my_setting.setOnClickListener(this)
    }

    override fun visibleToUser() {
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.mine)
    }

    override fun onClick(v: View?) {
        when (v) {
            iv_avator -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
            }
            tv_preview_and_submit_xls -> startActivity<PreviewXlsProjectActivity>()
            tv_my_setting -> startActivity<SettingActivity>()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_CODE_PICK_IMAGE -> {
                if (data?.data == null) {
                    ToastUtils.showToast("数据为空")
                    return
                }

                val intent = Intent("com.android.camera.action.CROP")
                intent.setDataAndType(data.data, "image/*")
                intent.putExtra("crop", "true")
                // aspectX aspectY 是宽高的比例
                intent.putExtra("aspectX", 1)
                intent.putExtra("aspectY", 1)
                // outputX outputY 是裁剪图片宽高
                intent.putExtra("outputX", 100)
                intent.putExtra("outputY", 100)
                intent.putExtra("noFaceDetection", true)
                intent.putExtra("return-data", true)//为true则返回bitmap

//                val file = File(context!!.getExternalFilesDir("img"), "${System.currentTimeMillis()}.jpg")
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE)
            }
            REQUEST_CODE_CROP_IMAGE -> {
                val bitmap = data?.getParcelableExtra<Bitmap>("data")
                if (bitmap == null) {
                    ToastUtils.showToast("数据为空")
                    return
                }

                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
                RetrofitManager.apiService.modifyAvator(RequestBody.create(okhttp3.MediaType.parse("text;charset=utf-8"), Base64.encode(bos.toByteArray(), Base64.DEFAULT))).enqueue(modifyCallback)
                bos.close()
            }
        }
    }

    private val modifyCallback by lazy {
        object : ResponseCallback<String>() {

            override fun onStart() = view != null

            override fun onSuccess(data: String) {
                Glide.with(context!!).load(data).into(iv_avator)
                BaseApplication.USER_INFO?.avator = data
                BaseApplication.USER_INFO = BaseApplication.USER_INFO
                ToastUtils.showToast("修改成功")
            }

            override fun onError(message: String) {
                ToastUtils.showToast(message)
            }

            override fun onNoNet(message: String) {
                ToastUtils.showToast(message)
            }

        }
    }

}