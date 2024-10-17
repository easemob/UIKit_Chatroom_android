package com.hyphenate.chatroom.service

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.hyphenate.chat.EMMessage
import com.hyphenate.chatroom.service.cache.UIChatroomCacheManager
import com.hyphenate.chatroom.service.model.UIChatroomInfo
import com.hyphenate.chatroom.service.model.UIConstant
import com.hyphenate.chatroom.service.model.UserInfoProtocol
import com.hyphenate.chatroom.service.model.toUser
import com.hyphenate.chatroom.service.serviceImpl.ChatroomServiceImpl
import com.hyphenate.chatroom.service.serviceImpl.UserServiceImpl
import com.hyphenate.chatroom.service.utils.GsonTools
import org.json.JSONObject

class ChatroomUIKitClient {
    private var currentRoomContext:UIChatroomContext = UIChatroomContext()
    private var chatroomUser:UIChatroomUser = UIChatroomUser()
    private var eventListeners = mutableListOf<ChatroomChangeListener>()
    private var giftListeners = mutableListOf<GiftReceiveListener>()
    private var userStateChangeListeners = mutableListOf<UserStateChangeListener>()
    private val roomEventResultListener: MutableList<ChatroomResultListener> by lazy { mutableListOf() }
    private val cacheManager: UIChatroomCacheManager = UIChatroomCacheManager()
    private val messageListener by lazy { InnerChatMessageListener() }
    private val chatroomChangeListener by lazy { InnerChatroomChangeListener() }
    private val userStateChangeListener by lazy { InnerUserStateChangeListener() }
    private val userService: UserService by lazy { UserServiceImpl() }
    private val chatroomService: ChatroomService by lazy { ChatroomServiceImpl() }
    private var uiOptions: UiOptions = UiOptions()

    companion object {
        const val TAG = "ChatroomUIKitClient"
        private var shared: ChatroomUIKitClient? = null

        fun getInstance(): ChatroomUIKitClient {
            if (shared == null) {
                synchronized(ChatroomUIKitClient::class.java) {
                    if (shared == null) {
                        shared = ChatroomUIKitClient()
                    }
                }
            }
            return shared!!
        }
    }

    /**
     * Init the chatroom ui kit
     */
    fun setUp(
        applicationContext: Context,
        appKey:String,
        options: ChatroomUIKitOptions = ChatroomUIKitOptions(),
    ){
        currentRoomContext.setRoomContext(applicationContext)
        uiOptions = options.uiOptions

        val chatOptions = ChatOptions()
        chatOptions.appKey = appKey
        chatOptions.autoLogin = options.chatOptions.autoLogin
        ChatClient.getInstance().init(applicationContext,chatOptions)
        ChatClient.getInstance().setDebugMode(options.chatOptions.enableDebug)
        cacheManager.init(applicationContext)
        registerConnectListener()
    }

    /**
     * Login the chat SDK
     * @param userId The user id
     * @param token The user token
     * @param onSuccess The callback to indicate the user login successfully
     * @param onError The callback to indicate the user login failed
     */
    fun login(userId: String, token: String, onSuccess: OnSuccess, onError: OnError) {
        if (!ChatClient.getInstance().isSdkInited) {
            onError.invoke(ChatError.GENERAL_ERROR,"SDK not initialized")
            return
        }
        userService.login(userId, token, onSuccess, onError)
    }

    /**
     * Login the chat SDK
     * @param user The user info
     * @param token The user token
     * @param onSuccess The callback to indicate the user login successfully
     * @param onError The callback to indicate the user login failed
     */
    fun login(user: UserInfoProtocol, token: String, onSuccess: OnSuccess, onError: OnError) {
        if (!ChatClient.getInstance().isSdkInited) {
            onError.invoke(ChatError.GENERAL_ERROR,"SDK not initialized")
            return
        }
        userService.login(user, token, onSuccess, onError)
    }

    /**
     * Logout the chat SDK
     * @param onSuccess The callback to indicate the user logout successfully
     * @param onError The callback to indicate the user logout failed
     */
    fun logout(onSuccess: OnSuccess, onError: OnError) {
        if (!ChatClient.getInstance().isSdkInited) {
            onError.invoke(ChatError.GENERAL_ERROR,"SDK not initialized")
            return
        }
        ChatClient.getInstance().logout(false, CallbackImpl(onSuccess, onError))
    }

    /**
     * Join a chatroom.
     * @param roomInfo The id of the chatroom info.
     * @param onSuccess The callback to indicate the user joined the chatroom successfully.
     * @param onError The callback to indicate the user failed to join the chatroom.
     */
    fun joinChatroom(roomInfo:UIChatroomInfo, onSuccess: OnValueSuccess<Chatroom> = {}, onError: OnError = { _, _ ->}) {
        if (!ChatClient.getInstance().isSdkInited) {
            onError.invoke(ChatError.GENERAL_ERROR,"SDK not initialized")
            return
        }
        initRoom(roomInfo)
        roomInfo.roomOwner?.userId?.let {
            chatroomService.joinChatroom(roomInfo.roomId, it, onSuccess, onError)
        }
    }

    /**
     * Init the chatroom before joining it
     */
    private fun initRoom(roomInfo: UIChatroomInfo){
        Log.e(TAG, "initRoom owner: ${roomInfo.roomOwner}")
        currentRoomContext.setCurrentRoomInfo(roomInfo)
        registerMessageListener()
        registerChatroomChangeListener()
    }

    /**
     * Register a room result listener.
     */
    @Synchronized
    fun registerRoomResultListener(listener: ChatroomResultListener){
        if (!roomEventResultListener.contains(listener)) {
            roomEventResultListener.add(listener)
        }
    }

    /**
     * Unregister a room result listener.
     */
    @Synchronized
    fun unregisterRoomResultListener(listener: ChatroomResultListener){
        if (roomEventResultListener.contains(listener)) {
            roomEventResultListener.remove(listener)
        }
    }

    fun updateUserInfo(userEntity: UserInfoProtocol, onSuccess: OnSuccess, onError: OnError){
        userService.updateUserInfo(userEntity,onSuccess,onError)
    }

    fun setCurrentTheme(isDark:Boolean){
        cacheManager.setCurrentTheme(isDark)
    }

    fun getCurrentTheme():Boolean{
        return cacheManager.getCurrentTheme()
    }

    fun getUseGiftsInMsg():Boolean{
        return uiOptions.chatBarrageShowGift
    }

    fun parseUserInfo(message: ChatMessage):UserInfoProtocol?{
        if (message.ext().containsKey(UIConstant.CHATROOM_UIKIT_USER_INFO)) {
            val jsonObject = message.getStringAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO)
            return GsonTools.toBean(jsonObject.toString(), UserInfoProtocol::class.java)
        }
        return null
    }

    @Synchronized
    internal fun callbackEvent(event: ChatroomResultEvent, errorCode: Int, errorMessage: String?) {
        if (roomEventResultListener.isEmpty()) {
            return
        }
        roomEventResultListener.iterator().forEach { listener ->
            listener.onEventResult(event, errorCode, errorMessage)
        }
    }

    fun getContext():UIChatroomContext{
        return currentRoomContext
    }

    fun getChatroomUser():UIChatroomUser{
        return chatroomUser
    }

    fun getCacheManager():UIChatroomCacheManager{
        return cacheManager
    }

    fun checkJoinedMsg(msg:ChatMessage):Boolean{
        val ext = msg.ext()
        return ext.containsKey(UIConstant.CHATROOM_UIKIT_USER_JOIN)
    }

    fun isCurrentRoomOwner(ownerId:String? = ""):Boolean{
        return currentRoomContext.isCurrentOwner(ownerId)
    }

    /**
     * Check if the user has logged into the SDK before
     */
    fun isLoginBefore():Boolean{
       return ChatClient.getInstance().isSdkInited && ChatClient.getInstance().isLoggedInBefore
    }

    fun getTranslationLanguage():List<String>{
        return uiOptions.targetLanguageList
    }

    fun getCurrentUser():UserEntity{
        val currentUser = ChatClient.getInstance().currentUser
        return chatroomUser.getUserInfo(currentUser)
    }

    fun insertJoinedMessage(roomId:String,userId:String):ChatMessage{
        val joinedUserInfo = chatroomUser.getUserInfo(userId)
        val joinedMessage:ChatMessage
        if (userId == getCurrentUser().userId){
            joinedMessage = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
            joinedMessage.to = roomId
        }else{
            joinedMessage = ChatMessage.createReceiveMessage(ChatMessageType.CUSTOM)
            joinedMessage.from = userId
            joinedMessage.to = roomId
        }
        val customMessageBody = ChatCustomMessageBody(UIConstant.CHATROOM_UIKIT_USER_JOIN)
        joinedMessage.addBody(customMessageBody)
        val jsonString = GsonTools.beanToString(joinedUserInfo)
        joinedMessage.setAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO, jsonString?.let { JSONObject(it) })
        return joinedMessage
    }

    fun sendJoinedMessage(){
        val joinedUserInfo = getCurrentUser()
        val roomId = getContext().getCurrentRoomInfo().roomId
        val joinedMsg = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
        joinedMsg.to = roomId
        val customMessageBody = ChatCustomMessageBody(UIConstant.CHATROOM_UIKIT_USER_JOIN)
        joinedMsg.addBody(customMessageBody)
        val jsonString = GsonTools.beanToString(joinedUserInfo)
        joinedMsg.setAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO, jsonString?.let { JSONObject(it) })
        chatroomService.sendMessage(
            joinedMsg, onSuccess = {},
            onError = {code, error ->
                ChatLog.e("sendJoinedMessage","sendJoinedMessage onError $code $error")
            })
    }


    @Synchronized
    fun clear(){
        eventListeners.clear()
        giftListeners.clear()
        userStateChangeListeners.clear()
        unRegisterMessageListener()
        unRegisterChatroomChangeListener()
    }

    @Synchronized
    fun clearUserStateChangeListener(){
        userStateChangeListeners.clear()
    }

    internal fun updateChatroomChangeListener(listener:MutableList<ChatroomChangeListener>){
        this.eventListeners = listener
    }

    internal fun updateChatroomGiftListener(listener:MutableList<GiftReceiveListener>){
        this.giftListeners = listener
    }

    internal fun updateChatroomUserStateChangeListener(listener:MutableList<UserStateChangeListener>){
        this.userStateChangeListeners = listener
    }

    private fun registerMessageListener() {
        ChatClient.getInstance().chatManager().addMessageListener(messageListener)
    }

    private fun unRegisterMessageListener(){
        ChatClient.getInstance().chatManager().removeMessageListener(messageListener)
    }

    private fun registerChatroomChangeListener() {
        ChatClient.getInstance().chatroomManager().addChatRoomChangeListener(chatroomChangeListener)
    }

    private fun unRegisterChatroomChangeListener(){
        ChatClient.getInstance().chatroomManager().removeChatRoomListener(chatroomChangeListener)
    }

    private fun registerConnectListener() {
        ChatClient.getInstance().addConnectionListener(userStateChangeListener)
    }

    private inner class InnerChatroomChangeListener: ChatRoomChangeListener {
        override fun onChatRoomDestroyed(roomId: String, roomName: String) {
            callbackEvent(ChatroomResultEvent.DESTROY_ROOM, ChatError.EM_NO_ERROR, "")
            clear()
        }

        override fun onMemberJoined(roomId: String?, participant: String?) {}

        override fun onMemberExited(roomId: String, roomName: String, participant: String) {
            try {
                for (listener in eventListeners.iterator()) {
                    listener.onUserLeft(roomId,roomName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onRemovedFromChatRoom(
            reason: Int,
            roomId: String,
            roomName: String,
            participant: String
        ) {
            try {
                for (listener in eventListeners.iterator()) {
                    listener.onUserBeKicked(roomId,roomName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onMuteListAdded(
            chatRoomId: String,
            mutes: MutableList<String>,
            expireTime: Long
        ) {
            try {
                for (listener in eventListeners.iterator()) {
                    if (mutes.size > 0){
                        for (mute in mutes) {
                            listener.onUserMuted(chatRoomId,mute)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onMuteListRemoved(chatRoomId: String, mutes: MutableList<String>) {
            try {
                for (listener in eventListeners.iterator()) {
                    if (mutes.size > 0){
                        for (mute in mutes) {
                            listener.onUserUnmuted(chatRoomId,mute)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onWhiteListAdded(chatRoomId: String?, whitelist: MutableList<String>?) {}

        override fun onWhiteListRemoved(chatRoomId: String?, whitelist: MutableList<String>?) {}

        override fun onAllMemberMuteStateChanged(chatRoomId: String?, isMuted: Boolean) {}

        override fun onAdminAdded(chatRoomId: String, admin: String) {
            try {
                for (listener in eventListeners.iterator()) {
                    listener.onAdminAdded(chatRoomId,admin)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onAdminRemoved(chatRoomId: String, admin: String) {
            try {
                for (listener in eventListeners.iterator()) {
                    listener.onAdminRemoved(chatRoomId,admin)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onOwnerChanged(chatRoomId: String?, newOwner: String?, oldOwner: String?) {}

        override fun onAnnouncementChanged(chatRoomId: String, announcement: String) {
            try {
                for (listener in eventListeners.iterator()) {
                    listener.onAnnouncementUpdated(chatRoomId,announcement)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private inner class InnerUserStateChangeListener: ChatConnectionListener {
        override fun onConnected() {
            try {
                for (listener in userStateChangeListeners.iterator()) {
                    listener.onConnected()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onDisconnected(errorCode: Int) {
            // Should listen onLogout in below cases:
            if (errorCode == ChatError.USER_LOGIN_ANOTHER_DEVICE
                || errorCode == ChatError.USER_REMOVED
                || errorCode == ChatError.USER_BIND_ANOTHER_DEVICE
                || errorCode == ChatError.USER_DEVICE_CHANGED
                || errorCode == ChatError.SERVER_SERVICE_RESTRICTED
                || errorCode == ChatError.USER_LOGIN_TOO_MANY_DEVICES
                || errorCode == ChatError.USER_KICKED_BY_CHANGE_PASSWORD
                || errorCode == ChatError.USER_KICKED_BY_OTHER_DEVICE
                || errorCode == ChatError.APP_ACTIVE_NUMBER_REACH_LIMITATION) {
                return
            }
            try {
                for (listener in userStateChangeListeners.iterator()) {
                    listener.onDisconnected(errorCode)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onTokenExpired() {
            try {
                for (listener in userStateChangeListeners.iterator()) {
                    listener.onTokenExpired()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onTokenWillExpire() {
            try {
                for (listener in userStateChangeListeners.iterator()) {
                    listener.onTokenWillExpire()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onLogout(errorCode: Int, info: String?) {
            try {
                for (listener in userStateChangeListeners.iterator()) {
                    listener.onLogout(errorCode, info)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private inner class InnerChatMessageListener: ChatMessageListener {

        override fun onMessageReceived(messages: MutableList<ChatMessage>?) {
            messages?.forEach {
                if (it.isBroadcast){
                    try {
                        for (listener in eventListeners.iterator()) {
                            listener.onBroadcastReceived(it)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }else{
                    if (it.type == ChatMessageType.TXT) {
                        try {
                            for (listener in eventListeners.iterator()) {
                                parseMsgUserInfo(it)?.let { userInfo->
                                    chatroomUser.setUserInfo(it.from,userInfo.toUser())
                                }
                                listener.onMessageReceived(it)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    // Check if it is a custom message first
                    if (it.type == ChatMessageType.CUSTOM) {
                        val body = it.body as ChatCustomMessageBody
                        val event = body.event()
                        val msgType: UICustomMsgType? = getCustomMsgType(event)

                        // Then exclude single chat
                        if (it.chatType != ChatType.Chat){
                            val username: String = it.to
                            // Check if it is the same chat room or group and event is not empty
                            if (TextUtils.equals(username,currentRoomContext.getCurrentRoomInfo().roomId) && !TextUtils.isEmpty(event)) {
                                when (msgType) {
                                    UICustomMsgType.CHATROOMUIKITUSERJOIN -> {
                                        try {
                                            for (listener in eventListeners.iterator()) {
                                                parseMsgUserInfo(it)?.let { userInfo->
                                                    chatroomUser.setUserInfo(it.from,userInfo.toUser())
                                                }
                                                listener.onUserJoined(it.conversationId(),it.from)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    UICustomMsgType.CHATROOMUIKITGIFT -> {
                                        try {
                                            for (listener in giftListeners.iterator()) {
                                                val giftEntity = parseGiftMsg(it)
                                                listener.onGiftReceived(
                                                    roomId = currentRoomContext.getCurrentRoomInfo().roomId,
                                                    gift = giftEntity,
                                                    message = it
                                                )
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun onMessageRecalled(messages: MutableList<EMMessage>?) {
            messages?.forEach {
                try {
                    for (listener in eventListeners.iterator()) {
                        listener.onRecallMessageReceived(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun getCustomMsgType(event: String?): UICustomMsgType? {
            return if (TextUtils.isEmpty(event)) {
                null
            } else UICustomMsgType.fromName(event)
        }

        private fun parseGiftMsg(msg: ChatMessage): GiftEntityProtocol? {
            val userEntity = parseUserInfo(msg)?.toUser()
            userEntity?.let {
                chatroomUser.setUserInfo(msg.from, it)
            }
            if (msg.body is ChatCustomMessageBody){
                val customBody = msg.body as ChatCustomMessageBody
                if (customBody.params.containsKey(UIConstant.CHATROOM_UIKIT_GIFT_INFO)){
                    val gift = customBody.params[UIConstant.CHATROOM_UIKIT_GIFT_INFO]
                    val giftEntityProtocol = GsonTools.toBean(gift, GiftEntityProtocol::class.java)
                    userEntity?.let {
                        giftEntityProtocol?.sendUser = it.transfer()
                    }
                    return giftEntityProtocol
                }
            }
            return null
        }

        private fun parseMsgUserInfo(msg: ChatMessage):UserInfoProtocol? {
            if (msg.ext().containsKey(UIConstant.CHATROOM_UIKIT_USER_INFO)){
                return try {
                    val jsonObject = msg.getStringAttribute(UIConstant.CHATROOM_UIKIT_USER_INFO)
                    return GsonTools.toBean(jsonObject.toString(), UserInfoProtocol::class.java)
                }catch (e:ChatException){
                    null
                }
            }
            return null
        }

    }
}