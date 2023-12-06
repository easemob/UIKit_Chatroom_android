package com.hyphenate.chatroom.bean

import com.google.gson.Gson

/**
 * The response of the login request.
 */
data class LoginRes(
    val userName: String,
    val icon_key: String,
    val access_token: String,
    val expires_in: Long,
)

fun LoginRes.toJson(): String {
    return Gson().toJson(this)
}

/**
 * The response of request list.
 */
data class RequestListResp<T>(
    val entities: List<T>,
    val count: Int,
)

/**
 * The response of sending broadcast.
 */
data class BroadcastReq(
    val data: BroadcastResData,
)

data class BroadcastResData(
    val id: String,
)

/**
 * The request params of creating room.
 */
data class CreateRoomReq(
    val name: String,
    val owner: String,
)



