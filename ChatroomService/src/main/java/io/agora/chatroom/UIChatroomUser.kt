package io.agora.chatroom

import io.agora.chatroom.service.UserEntity

class UIChatroomUser {

    fun getUserInfo(userId:String): UserEntity {
        return ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
    }

    fun setUserInfo(userId:String,userInfo: UserEntity){
        ChatroomUIKitClient.getInstance().getCacheManager().saveUserInfo(userId,userInfo)
    }
}