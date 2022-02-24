package com.example.appsocket.model

import java.util.*

sealed class Messages {

    data class MsgSender(
        var msgOutGoing: String? = null,
        var date: Date? = null

    ) : Messages()


    data class MsgReceiver(
        var msgIncoming: String? = null,
        var date: Date? = null
    ) : Messages()


}







