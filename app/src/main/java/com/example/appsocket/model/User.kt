package com.example.appsocket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val email: String = "",
    val id: String = "",
    var isOnline: Boolean = false

) : Parcelable
