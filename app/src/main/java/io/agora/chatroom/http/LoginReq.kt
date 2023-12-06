package io.agora.chatroom.http

import com.google.gson.Gson
import io.agora.chatroom.model.UserInfoProtocol

data class LoginReq(
    val username: String,
    val nickname: String,
    val icon_key: String
)

fun LoginReq.toUserProtocol(): UserInfoProtocol {
    return UserInfoProtocol(userId = username, nickName = nickname, avatarURL = icon_key)
}

fun UserInfoProtocol.toLoginReq(): LoginReq {
    return LoginReq(username = userId, nickname = nickName?:"", icon_key = avatarURL?:"")
}

fun LoginReq.toJson(): String {
    return Gson().toJson(this.toUserProtocol())
}
