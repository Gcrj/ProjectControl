package com.gcrj.projectcontrol.util

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.gcrj.projectcontrol.R

inline fun <reified T> Activity.startActivity() {
    startActivity(Intent(this, T::class.java))
    overridePendingTransition(R.anim.anim_in_from_right, R.anim.anim_out_to_left)
}

inline fun <reified T> Activity.startActivity(data: (intent: Intent) -> Unit) {
    val intent = Intent(this, T::class.java)
    data.invoke(intent)
    startActivity(intent)
    overridePendingTransition(R.anim.anim_in_from_right, R.anim.anim_out_to_left)
}

inline fun <reified T> Activity.startActivityForResult(requestCode: Int) {
    startActivityForResult(Intent(this, T::class.java), requestCode)
    overridePendingTransition(R.anim.anim_in_from_right, R.anim.anim_out_to_left)
}

inline fun <reified T> Activity.startActivityForResult(data: (intent: Intent) -> Unit, requestCode: Int) {
    val intent = Intent(this, T::class.java)
    data.invoke(intent)
    startActivityForResult(intent, requestCode)
    overridePendingTransition(R.anim.anim_in_from_right, R.anim.anim_out_to_left)
}

inline fun <reified T> Fragment.startActivity() {
    activity?.startActivity<T>()
}

inline fun <reified T> Fragment.startActivity(data: (intent: Intent) -> Unit) {
    activity?.startActivity<T>(data)
}

inline fun <reified T> Fragment.startActivityForResult(requestCode: Int) {
    startActivityForResult(Intent(activity, T::class.java), requestCode)
    activity?.overridePendingTransition(R.anim.anim_in_from_right, R.anim.anim_out_to_left)
}

inline fun <reified T> Fragment.startActivityForResult(data: (intent: Intent) -> Unit, requestCode: Int) {
    val intent = Intent(activity, T::class.java)
    data.invoke(intent)
    startActivityForResult(intent, requestCode)
    activity?.overridePendingTransition(R.anim.anim_in_from_right, R.anim.anim_out_to_left)
}