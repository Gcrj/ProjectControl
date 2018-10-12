package com.gcrj.projectcontrol.bean

import android.os.Parcel
import android.os.Parcelable

class CheckUpdateBean() : Parcelable {

    var hasUpdate: Boolean? = null
    var id: Int? = null
    var url: String? = null
    var message: String? = null
    var version_code: Int? = null
    var version_name: String? = null
    var force_update: Boolean? = null

    constructor(parcel: Parcel) : this() {
        hasUpdate = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        url = parcel.readString()
        message = parcel.readString()
        version_code = parcel.readValue(Int::class.java.classLoader) as? Int
        version_name = parcel.readString()
        force_update = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(hasUpdate)
        parcel.writeValue(id)
        parcel.writeString(url)
        parcel.writeString(message)
        parcel.writeValue(version_code)
        parcel.writeString(version_name)
        parcel.writeValue(force_update)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckUpdateBean> {
        override fun createFromParcel(parcel: Parcel): CheckUpdateBean {
            return CheckUpdateBean(parcel)
        }

        override fun newArray(size: Int): Array<CheckUpdateBean?> {
            return arrayOfNulls(size)
        }
    }

}