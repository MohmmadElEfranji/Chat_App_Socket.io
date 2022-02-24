package com.example.appsocket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(var id: String = "", var name: String = "", var emailUser: ArrayList<String>) :
    Parcelable


