package com.hyphenate.chatroom.viewmodel.messages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessagePinInfo
import com.hyphenate.chatroom.ChatroomUIKitClient
import com.hyphenate.chatroom.UIChatroomService
import com.hyphenate.chatroom.commons.ComposeChatListController
import com.hyphenate.chatroom.commons.ComposeMessageListState
import com.hyphenate.chatroom.service.ChatMessage
import com.hyphenate.chatroom.compose.messagelist.ComposeMessageListItemState
import com.hyphenate.chatroom.service.ChatLog
import com.hyphenate.chatroom.service.ChatroomChangeListener
import com.hyphenate.chatroom.service.GiftEntityProtocol
import com.hyphenate.chatroom.service.GiftReceiveListener
import com.hyphenate.chatroom.service.OnError
import com.hyphenate.chatroom.service.OnSuccess
import com.hyphenate.chatroom.service.OnValueSuccess
import com.hyphenate.util.EMLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageListViewModel(
    private val isDarkTheme: Boolean? = false,
    private val showDateSeparators: Boolean = true,
    private val showLabel: Boolean = false,
    private val showAvatar: Boolean = true,
    private val roomId: String,
    private val chatService: UIChatroomService,
    private val composeChatListController: ComposeChatListController
    ): ViewModel(), ChatroomChangeListener, GiftReceiveListener {

    private val _pinMessage = MutableLiveData<ChatMessage?>()

    val pinMessage: LiveData<ChatMessage?> = _pinMessage

    fun updatePinMessage(message: ChatMessage?) {
        viewModelScope.launch(Dispatchers.Main) {
            _pinMessage.value = message
        }
    }

    /**
     * Register chatroom change listener
     */
    fun registerChatroomChangeListener() {
        chatService.getChatService().bindListener(this)
        chatService.getGiftService().bindGiftListener(this)
    }


    /**
     * Unregister chatroom change listener
     */
    private fun unRegisterChatroomChangeListener() {
        chatService.getChatService().unbindListener(this)
        chatService.getGiftService().unbindGiftListener(this)
    }

    /**
     * Register chatroom gift listener
     */
    fun registerChatroomGiftListener() {
        chatService.getGiftService().bindGiftListener(this)
    }

    /**
     * Unregister chatroom gift listener
     */
    private fun unRegisterChatroomGiftListener() {
        chatService.getGiftService().unbindGiftListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        unRegisterChatroomChangeListener()
        unRegisterChatroomGiftListener()
    }

    override fun onMessageReceived(message: ChatMessage) {
        super.onMessageReceived(message)
        addTextMessageByIndex(message = message)
    }

    override fun onBroadcastReceived(message: ChatMessage) {
        super.onBroadcastReceived(message)
    }

    override fun onRecallMessageReceived(message: ChatMessage) {
        super.onRecallMessageReceived(message)
        if (message.conversationId() == ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomId){
            removeMessage(message = message)
        }
    }

    override fun onMessagePinChanged(
        messageId: String?,
        conversationId: String?,
        pinOperation: EMMessagePinInfo.PinOperation?,
        pinInfo: EMMessagePinInfo?){
        if (pinOperation == EMMessagePinInfo.PinOperation.PIN) {
            EMClient.getInstance().chatManager().getMessage(messageId)?.let {
                updatePinMessage(it)
            }
        }else{
            updatePinMessage(null)
        }
    }

    override fun onUserJoined(roomId: String, userId: String) {
        addJoinedMessageByIndex(
            message = ChatroomUIKitClient.getInstance().insertJoinedMessage(roomId,userId)
        )
    }

    override fun onGiftReceived(roomId: String, gift: GiftEntityProtocol?, message: ChatMessage) {
        super.onGiftReceived(roomId, gift, message)
        if (ChatroomUIKitClient.getInstance().getUseGiftsInMsg()){
            gift?.let { giftEntity ->
                ChatroomUIKitClient.getInstance().parseUserInfo(message)?.let{
                    giftEntity.sendUser = it
                }
                addGiftMessageByIndex(message = message, gift = giftEntity)
            }
        }
    }

    /**
     * Send gift message
     */
    fun sendGift(gift: GiftEntityProtocol, onSuccess: OnValueSuccess<ChatMessage>, onError: OnError) {
        chatService.getGiftService().sendGift(gift = gift, onSuccess = {
            message ->
            onSuccess.invoke(message)
        }, onError)
    }

    /**
     * Send a text message.
     */
    fun sendTextMessage(message: String,
                        onSuccess: (ChatMessage) -> Unit = {},
                        onError: OnError = {_, _ ->}) {
        chatService.getChatService().sendTextMessage(message, roomId, onSuccess = {
            msg ->
            addTextMessageByIndex(message = msg)
            onSuccess.invoke(msg)
        }, onError)
    }

    fun translateMessage(message: ChatMessage,onSuccess: OnSuccess = {},onError: OnError = {code,error ->}) {
        chatService.getChatService().translateTextMessage(message, onSuccess = {
                msg ->
            updateTextMessage(message = msg)
            onSuccess.invoke()
        }, onError = {code, error ->
            Log.e("MessageListViewModel","translateMessage onError $code $error")
            onError.invoke(code,error)
        })
    }

    fun removeMessage(message: ChatMessage?, onSuccess: OnSuccess, onError: OnError) {
        chatService.getChatService().recallMessage(message, onSuccess = {
            onSuccess.invoke()
        }, onError)
    }

    fun pinMessage(message: ChatMessage?, onSuccess: OnSuccess, onError: OnError) {
        chatService.getChatService().pinMessage(message?.msgId?:"", onSuccess = {
            onSuccess.invoke()
            updatePinMessage(message)
        }, onError)
    }

    fun unpinMessage(message: ChatMessage?, onSuccess: OnSuccess, onError: OnError) {
        chatService.getChatService().unpinMessage(message?.msgId?:"", onSuccess = {
            updatePinMessage(null)
            onSuccess.invoke()
        }, onError)
    }

    fun fetchPinMessagesFromServer() {
        chatService.getChatService().fetchPinMessageFromServer(roomId,onSuccess={
           //获取第一个元素
            it.firstOrNull()?.let { message ->
                ChatLog.i("MessageListViewModel","fetchPinMessagesFromServer $message")
                updatePinMessage(message)
            }
        }, onError={code, error ->
            ChatLog.i("MessageListViewModel","fetchPinMessagesFromServer onError $code $error")
            updatePinMessage(null) })
    }

    fun getPinMessagesFromLocal() {
        chatService.getChatService().getPinMessageFromLocal(roomId,onSuccess={
            //获取第一个元素
            it.firstOrNull()?.let { message ->
                updatePinMessage(message)
            }
        }, onError={code, error -> updatePinMessage(null) })
    }

    fun addTextMessage(message:ChatMessage){
        composeChatListController.addTextMessage(message)
    }

    fun addTextMessageByIndex(index:Int = 0,message:ChatMessage){
        composeChatListController.addTextMessage(index = index,message = message)
    }

    fun addGiftMessageByIndex(index:Int = 0,message:ChatMessage,gift:GiftEntityProtocol){
        composeChatListController.addGiftMessage(index = index,message = message, gift = gift)
    }

    fun addGiftMessage(message:ChatMessage,gift:GiftEntityProtocol){
        composeChatListController.addGiftMessage(message,gift)
    }

    fun addJoinedMessageByIndex(index:Int = 0,message:ChatMessage){
        composeChatListController.addJoinedMessage(index = index, message = message)
    }

    fun addJoinedMessage(message:ChatMessage){
        composeChatListController.addJoinedMessage(message)
    }

    fun removeMessage(message: ComposeMessageListItemState){
        composeChatListController.removeMessage(message)
    }

    fun removeMessage(message: ChatMessage): Boolean {
        composeChatListController.getMessage(message.msgId)?.let {
            composeChatListController.removeMessage(it)
            return true
        }
        return false
    }

    fun removeMessageByIndex(index: Int){
        composeChatListController.removeMessageByIndex(index)
    }

    fun updateTextMessage(message: ChatMessage){
        composeChatListController.updateTextMessage(message = message)
    }

    fun clearMessage(){
        composeChatListController.clearMessage()
    }

    val currentComposeMessageListState: ComposeMessageListState
        get() = composeChatListController.currentComposeMessageListState


    val getTheme: Boolean?
        get() = isDarkTheme

    val isShowDateSeparators:Boolean
        get() = showDateSeparators

    val isShowLabel:Boolean
        get() = showLabel

    val isShowAvatar:Boolean
        get() = showAvatar

}