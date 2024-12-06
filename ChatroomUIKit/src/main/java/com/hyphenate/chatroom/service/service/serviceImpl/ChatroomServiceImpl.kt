package com.hyphenate.chatroom.service.serviceImpl

import com.hyphenate.chat.EMClient
import com.hyphenate.chatroom.ChatroomResultEvent
import com.hyphenate.chatroom.ChatroomUIKitClient
import com.hyphenate.chatroom.model.UIConstant
import com.hyphenate.chatroom.service.CallbackImpl
import com.hyphenate.chatroom.service.ChatCallback
import com.hyphenate.chatroom.service.ChatClient
import com.hyphenate.chatroom.service.ChatCursorResult
import com.hyphenate.chatroom.service.ChatCustomMessageBody
import com.hyphenate.chatroom.service.ChatError
import com.hyphenate.chatroom.service.ChatMessage
import com.hyphenate.chatroom.service.ChatMessageType
import com.hyphenate.chatroom.service.ChatType
import com.hyphenate.chatroom.service.Chatroom
import com.hyphenate.chatroom.service.ChatroomChangeListener
import com.hyphenate.chatroom.service.ChatroomService
import com.hyphenate.chatroom.service.OnError
import com.hyphenate.chatroom.service.OnSuccess
import com.hyphenate.chatroom.service.OnValueSuccess
import com.hyphenate.chatroom.service.UserOperationType
import com.hyphenate.chatroom.service.ValueCallbackImpl
import com.hyphenate.chatroom.utils.GsonTools
import org.json.JSONObject

class ChatroomServiceImpl: ChatroomService {

    private val listeners = mutableListOf<ChatroomChangeListener>()
    private val chatroomManager by lazy { ChatClient.getInstance().chatroomManager() }
    private val chatManager by lazy { ChatClient.getInstance().chatManager() }
    @Synchronized
    override fun bindListener(listener: ChatroomChangeListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
            ChatroomUIKitClient.getInstance().updateChatroomChangeListener(listeners)
        }
    }

    @Synchronized
    override fun unbindListener(listener: ChatroomChangeListener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener)
            ChatroomUIKitClient.getInstance().updateChatroomChangeListener(listeners)
        }
    }

    override fun joinChatroom(
        roomId: String,
        userId: String,
        onSuccess: OnValueSuccess<Chatroom>,
        onError: OnError
    ) {
        if (userId.isEmpty() or roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatroomManager.joinChatRoom(roomId, ValueCallbackImpl(onSuccess, onError, event = ChatroomResultEvent.JOIN_ROOM))
    }

    override fun leaveChatroom(
        roomId: String,
        userId: String,
        onSuccess: OnSuccess,
        onError: OnError
    ) {
        if (userId.isEmpty() or roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatroomManager.leaveChatRoom(roomId, CallbackImpl(onSuccess, onError, event = ChatroomResultEvent.LEAVE_ROOM))
    }

    override fun destroyChatroom(
        roomId: String,
        onSuccess: OnSuccess,
        onError: OnError
    ) {
        chatroomManager.asyncDestroyChatRoom(roomId,CallbackImpl(onSuccess, onError, event = ChatroomResultEvent.DESTROY_ROOM))
    }

    override fun fetchMembers(
        roomId: String,
        cursor: String?,
        pageSize: Int,
        onSuccess: OnValueSuccess<ChatCursorResult<String>>,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatroomManager.asyncFetchChatRoomMembers(roomId, cursor, pageSize, ValueCallbackImpl(onSuccess, onError, event = ChatroomResultEvent.FETCH_MEMBERS))
    }

    override fun fetchMuteList(
        roomId: String,
        pageNum: Int,
        pageSize: Int,
        onSuccess: OnValueSuccess<Map<String, Long>>,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatroomManager.asyncFetchChatRoomMuteList(roomId, pageNum, pageSize, ValueCallbackImpl(onSuccess, onError, event = ChatroomResultEvent.FETCH_MUTES))
    }

    override fun getAnnouncement(
        roomId: String,
        onSuccess: OnValueSuccess<String?>,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatroomManager.asyncFetchChatRoomAnnouncement(roomId, ValueCallbackImpl<String>(onSuccess, onError, event = ChatroomResultEvent.ANNOUNCEMENT))
    }

    override fun updateAnnouncement(
        roomId: String,
        announcement: String,
        onSuccess: OnSuccess,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatroomManager.asyncUpdateChatRoomAnnouncement(roomId, announcement, CallbackImpl(onSuccess, onError, event = ChatroomResultEvent.ANNOUNCEMENT))
    }

    override fun operateUser(
        roomId: String,
        userId: String,
        operation: UserOperationType,
        onSuccess: OnValueSuccess<Chatroom>,
        onError: OnError
    ) {
        if (userId.isEmpty() or roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        when(operation) {
            UserOperationType.ADD_ADMIN  -> {
                chatroomManager.asyncAddChatRoomAdmin(roomId, userId, ValueCallbackImpl(onSuccess, onError))
            }
            UserOperationType.REMOVE_ADMIN -> {
                chatroomManager.asyncRemoveChatRoomAdmin(roomId, userId, ValueCallbackImpl(onSuccess, onError))
            }
            UserOperationType.MUTE -> {
                chatroomManager.asyncMuteChatRoomMembers(roomId, mutableListOf(userId), -1, ValueCallbackImpl(onSuccess, onError, event = ChatroomResultEvent.MUTE_MEMBER))
            }
            UserOperationType.UNMUTE -> {
                chatroomManager.asyncUnMuteChatRoomMembers(roomId, mutableListOf(userId), ValueCallbackImpl(onSuccess, onError, event = ChatroomResultEvent.UNMUTE_MEMBER))
            }
            UserOperationType.BLOCK -> {
                chatroomManager.asyncBlockChatroomMembers(roomId, mutableListOf(userId), ValueCallbackImpl(onSuccess, onError))
            }
            UserOperationType.UNBLOCK -> {
                chatroomManager.asyncUnBlockChatRoomMembers(roomId, mutableListOf(userId), ValueCallbackImpl(onSuccess, onError))
            }
            UserOperationType.KICK -> {
                chatroomManager.asyncRemoveChatRoomMembers(roomId, mutableListOf(userId), ValueCallbackImpl(onSuccess, onError, event = ChatroomResultEvent.KICK_MEMBER))
            }
        }
    }

    override fun sendTextMessage(
        message: String,
        roomId: String,
        onSuccess: (ChatMessage) -> Unit,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        val textSendMessage = ChatMessage.createTextSendMessage(message, roomId)
        sendMessage(textSendMessage, onSuccess, onError) {}
    }

    override fun sendTargetTextMessage(
        targetUserIds: List<String>,
        message: String,
        roomId: String,
        onSuccess: (ChatMessage) -> Unit,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        val textSendMessage = ChatMessage.createTextSendMessage(message, roomId)
        textSendMessage?. setReceiverList(targetUserIds)
        sendMessage(textSendMessage, onSuccess, onError)
    }

    override fun sendTargetCustomMessage(
        targetUserIds: List<String>,
        event: String,
        ext: Map<String, String>,
        roomId: String,
        onSuccess: (ChatMessage) -> Unit,
        onError: OnError
    ) {
        if (roomId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        val customMessage = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
        val customBody = ChatCustomMessageBody(event)
        customBody.params = ext
        customMessage.body = customBody
        customMessage.setReceiverList(targetUserIds)
        sendMessage(customMessage, onSuccess, onError)
    }

    override fun sendMessage(
        message: ChatMessage?,
        onSuccess: (ChatMessage) -> Unit,
        onError: OnError,
        onProgress: (Int) -> Unit
    ) {
        if (message == null) {
            onError(ChatError.MESSAGE_INVALID, "")
            return
        }

        val currentUser = ChatroomUIKitClient.getInstance().getCurrentUser()
        val userInfo = GsonTools.beanToString(currentUser)
        val jsonObject = if (userInfo != null) {
            JSONObject(userInfo)
        }else{
            JSONObject()
        }
        message.setAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO,jsonObject)
        message.chatType = ChatType.ChatRoom
        message.setMessageStatusCallback(object : ChatCallback {
            override fun onSuccess() {
                onSuccess(message)
                ChatroomUIKitClient.getInstance().callbackEvent(ChatroomResultEvent.SEND_MESSAGE, ChatError.EM_NO_ERROR, "")
            }

            override fun onError(code: Int, error: String?) {
                onError(code, error)
                ChatroomUIKitClient.getInstance().callbackEvent(ChatroomResultEvent.SEND_MESSAGE, code, error)
            }

            override fun onProgress(progress: Int, status: String?) {
                onProgress(progress)
            }
        })
        chatManager.sendMessage(message)
    }

    override fun translateTextMessage(
        message: ChatMessage?,
        onSuccess: (ChatMessage) -> Unit,
        onError: OnError
    ) {
        chatManager.translateMessage(message,
            ChatroomUIKitClient.getInstance().getTranslationLanguage(),
            ValueCallbackImpl<ChatMessage>(onSuccess, onError, event = ChatroomResultEvent.TRANSLATE))
    }

    override fun recallMessage(message: ChatMessage?, onSuccess: OnSuccess, onError: OnError) {
        chatManager.asyncRecallMessage(message, CallbackImpl(onSuccess, onError, event = ChatroomResultEvent.RECALL_MESSAGE))
    }

    override fun reportMessage(
        messageId: String,
        tag: String,
        reason: String,
        onSuccess: OnSuccess,
        onError: OnError
    ) {
        if (messageId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatManager.asyncReportMessage(messageId, tag, reason,
            CallbackImpl(onSuccess, onError, event = ChatroomResultEvent.REPORT))
    }

    override fun pinMessage(messageId: String, onSuccess: OnSuccess, onError: OnError) {
        if (messageId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatManager.asyncPinMessage(messageId, CallbackImpl(onSuccess, onError, event = ChatroomResultEvent.PIN_MESSAGE))

    }

    override fun unpinMessage(messageId: String, onSuccess: OnSuccess, onError: OnError) {
        if (messageId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        chatManager.asyncUnPinMessage(messageId, CallbackImpl(onSuccess, onError, event = ChatroomResultEvent.UNPIN_MESSAGE))

    }

    override fun fetchPinMessageFromServer(conversationId: String,
                                  onSuccess: OnValueSuccess<List<ChatMessage>>,
                                  onError: OnError){
        if (conversationId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        EMClient.getInstance().chatManager().asyncGetPinnedMessagesFromServer(conversationId,ValueCallbackImpl<List<ChatMessage>>(onSuccess, onError))
    }

    override fun getPinMessageFromLocal(conversationId: String,
                               onSuccess: OnValueSuccess<List<ChatMessage>>,
                               onError: OnError){
        if (conversationId.isEmpty()) {
            onError(ChatError.INVALID_PARAM, "")
            return
        }
        EMClient.getInstance().chatManager().getConversation(conversationId)?.pinnedMessages()?.let {
            ValueCallbackImpl<List<ChatMessage>>(onSuccess, onError).onSuccess(it)
        }?: onError(ChatError.CHATROOM_INVALID_ID, "")

    }
}