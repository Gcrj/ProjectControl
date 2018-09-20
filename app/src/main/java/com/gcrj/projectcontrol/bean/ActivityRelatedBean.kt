package com.gcrj.projectcontrol.bean

import android.os.Parcel
import android.os.Parcelable

class ActivityRelatedBean() : Parcelable {

    var id: Int? = null
    var activity_id: Int? = null
    var name: String? = null
    var progress: Int? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        activity_id = parcel.readValue(Int::class.java.classLoader) as? Int
        name = parcel.readString()
        progress = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(activity_id)
        parcel.writeString(name)
        parcel.writeValue(progress)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActivityRelatedBean> {
        override fun createFromParcel(parcel: Parcel): ActivityRelatedBean {
            return ActivityRelatedBean(parcel)
        }

        override fun newArray(size: Int): Array<ActivityRelatedBean?> {
            return arrayOfNulls(size)
        }
    }

}