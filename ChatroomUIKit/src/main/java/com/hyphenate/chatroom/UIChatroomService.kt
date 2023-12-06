package com.hyphenate.chatroom

import com.hyphenate.chatroom.model.UIChatroomInfo
import com.hyphenate.chatroom.service.Chatroom
import com.hyphenate.chatroom.service.ChatroomService
import com.hyphenate.chatroom.service.GiftService
import com.hyphenate.chatroom.service.OnValueSuccess
import com.hyphenate.chatroom.service.UserService
import com.hyphenate.chatroom.service.serviceImpl.ChatroomServiceImpl
import com.hyphenate.chatroom.service.serviceImpl.GiftServiceImpl
import com.hyphenate.chatroom.service.serviceImpl.UserServiceImpl

class UIChatroomService constructor(
    private var roomInfo: UIChatroomInfo
) {
    private val TAG = "UIChatroomService"

    private val chatImpl: ChatroomService by lazy { ChatroomServiceImpl() }

    private val giftImpl: GiftService by lazy { GiftServiceImpl() }

    private val userImpl:UserService by lazy { UserServiceImpl() }


    fun getGiftService() = giftImpl
    fun getChatService() = chatImpl
    fun getUserService() = userImpl
    fun getRoomInfo() = roomInfo

    fun joinRoom(onSuccess: OnValueSuccess<Chatroom>, onFailure: (Int,String?) -> Unit = { _, _ ->}){
        roomInfo.let {
            ChatroomUIKitClient.getInstance().joinChatroom( it,
                onSuccess = { chatroom ->
                    if (it.roomOwner != null && it.roomOwner!!.userId.isNotEmpty()) {
                        ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMemberList(it.roomId, listOf(it.roomOwner!!.userId))
                    }
                    ChatroomUIKitClient.getInstance().sendJoinedMessage()
                    onSuccess.invoke(chatroom)
                },
                onError = { code, error ->
                    onFailure.invoke(code,error)
                }
            )
        }
    }
}
