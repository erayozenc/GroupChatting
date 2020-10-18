package com.example.groupchatting.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(
    val name : String = "",
    val password : String = "",
    var groupId : String = "",
    var icon : String = ""
) : Parcelable