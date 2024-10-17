package com.hyphenate.chatroom.service.model

import com.hyphenate.chatroom.service.UserEntity
import java.io.Serializable

class UIChatroomInfo(
    var roomId:String,
    var roomOwner:UserEntity?

):UICreateRoomInfo(), Serializable