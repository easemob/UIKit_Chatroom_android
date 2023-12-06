package com.hyphenate.chatroom.model.marquee

data class UINotification(
    val content:MutableList<String> = mutableListOf(),
    val duration: Int = 3000,
//    val repeatCount: Int = 1,
//    val interval: Long = 0
)
