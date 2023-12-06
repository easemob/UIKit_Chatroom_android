package io.agora.chatroom.model

import io.agora.chatroom.service.ChatUserInfo
import io.agora.chatroom.service.UserEntity

fun UserInfoProtocol.transfer() = ChatUserInfo().run {

    this.userId = this@transfer.userId
    this.nickname = this@transfer.nickName
    this.avatarUrl = this@transfer.avatarURL
    this.gender = this@transfer.gender
    this.ext = this@transfer.identify
    this
}

fun UserInfoProtocol.toUser() = UserEntity(userId, nickName, avatarURL, gender, identify)
data class UserInfoProtocol(
    val userId: String,
    val nickName: String? = "",
    val avatarURL: String? = "",
    val gender: Int = 0,
    var identify:String? = ""
)