package io.agora.chatroom.model

import io.agora.chatroom.service.UserEntity
import java.io.Serializable

class UIChatroomInfo(
    var roomId:String,
    var roomOwner:UserEntity?

):UICreateRoomInfo(), Serializable