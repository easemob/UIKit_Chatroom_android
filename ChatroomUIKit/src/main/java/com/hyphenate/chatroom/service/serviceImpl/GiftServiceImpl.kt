package com.hyphenate.chatroom.service.serviceImpl

import com.hyphenate.chatroom.service.ChatCallback
import com.hyphenate.chatroom.service.ChatClient
import com.hyphenate.chatroom.service.ChatCustomMessageBody
import com.hyphenate.chatroom.service.ChatError
import com.hyphenate.chatroom.service.ChatMessage
import com.hyphenate.chatroom.service.ChatMessageType
import com.hyphenate.chatroom.service.ChatType
import com.hyphenate.chatroom.service.ChatroomResultEvent
import com.hyphenate.chatroom.service.ChatroomUIKitClient
import com.hyphenate.chatroom.service.GiftEntityProtocol
import com.hyphenate.chatroom.service.GiftReceiveListener
import com.hyphenate.chatroom.service.GiftService
import com.hyphenate.chatroom.service.OnError
import com.hyphenate.chatroom.service.OnValueSuccess
import com.hyphenate.chatroom.service.model.UIConstant
import com.hyphenate.chatroom.service.transfer
import com.hyphenate.chatroom.service.utils.GsonTools
import org.json.JSONObject

class GiftServiceImpl: GiftService {
    private val chatManager by lazy { ChatClient.getInstance().chatManager() }
    private val listeners = mutableListOf<GiftReceiveListener>()
    @Synchronized
    override fun bindGiftListener(listener: GiftReceiveListener) {
        if (!listeners.contains(listener)){
            listeners.add(listener)
            ChatroomUIKitClient.getInstance().updateChatroomGiftListener(listeners)
        }
    }

    @Synchronized
    override fun unbindGiftListener(listener: GiftReceiveListener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener)
            ChatroomUIKitClient.getInstance().updateChatroomGiftListener(listeners)
        }
    }

    override fun sendGift(entity: GiftEntityProtocol, onSuccess: OnValueSuccess<ChatMessage>, onError: OnError) {
        val message = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
        val customBody = ChatCustomMessageBody(UIConstant.CHATROOM_UIKIT_GIFT)
        val userInfoProtocol = ChatroomUIKitClient.getInstance().getCurrentUser().transfer()
        val user = GsonTools.beanToString(userInfoProtocol)
        val gift = GsonTools.beanToString(entity)
        val infoMap = mutableMapOf(UIConstant.CHATROOM_UIKIT_GIFT_INFO to gift)
        customBody.params = infoMap
        message.setAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO, user?.let { JSONObject(it) })
        message.chatType = ChatType.ChatRoom
        message.body = customBody
        message.to = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomId
        message.setMessageStatusCallback(object : ChatCallback{
            override fun onSuccess() {
                onSuccess.invoke(message)
                ChatroomUIKitClient.getInstance().callbackEvent(ChatroomResultEvent.SEND_MESSAGE, ChatError.EM_NO_ERROR, "")
            }

            override fun onError(code: Int, error: String?) {
                onError.invoke(code,error)
                ChatroomUIKitClient.getInstance().callbackEvent(ChatroomResultEvent.SEND_MESSAGE, code, error)
            }
        })
        chatManager.sendMessage(message)
    }
}