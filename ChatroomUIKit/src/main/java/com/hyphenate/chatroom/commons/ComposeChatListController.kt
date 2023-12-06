package com.hyphenate.chatroom.commons

import com.hyphenate.chatroom.service.ChatMessage
import com.hyphenate.chatroom.compose.messagelist.ComposeMessageItemState
import com.hyphenate.chatroom.compose.messagelist.ComposeMessageListItemState
import com.hyphenate.chatroom.compose.messagelist.GiftMessageState
import com.hyphenate.chatroom.compose.messagelist.JoinedMessageState
import com.hyphenate.chatroom.service.ChatTextMessageBody
import com.hyphenate.chatroom.service.GiftEntityProtocol

class ComposeChatListController(
    private val roomId: String,
    private val messageState:ComposeMessageListState,
){

    val currentComposeMessageListState: ComposeMessageListState
        get() = messageState

    fun addTextMessage(index:Int,message:ChatMessage){
        if (message.conversationId() == roomId){
            messageState.addMessageByIndex(index,ComposeMessageItemState(message,getTranslationContent(message)))
        }
    }

    fun addTextMessage(message:ChatMessage){
        if (message.conversationId() == roomId){
            messageState.addMessage(ComposeMessageItemState(message,getTranslationContent(message)))
        }
    }

    fun updateTextMessage(message: ChatMessage){
        if (message.conversationId() == roomId){
            messageState.updateMessage(ComposeMessageItemState(message,getTranslationContent(message)))
        }
    }

    fun addGiftMessage(index:Int,message:ChatMessage,gift:GiftEntityProtocol){
        if (message.conversationId() == roomId){
            messageState.addMessageByIndex(index,GiftMessageState(message,gift))
        }
    }

    fun addGiftMessage(message:ChatMessage,gift:GiftEntityProtocol){
        if (message.conversationId() == roomId){
            messageState.addMessage(GiftMessageState(message,gift))
        }
    }

    fun addJoinedMessage(index:Int,message:ChatMessage){
        if (message.conversationId() == roomId){
            messageState.addMessageByIndex(index,JoinedMessageState(message))
        }
    }

    fun addJoinedMessage(message:ChatMessage){
        if (message.conversationId() == roomId){
            messageState.addMessage(JoinedMessageState(message))
        }
    }

    /**
     * Returns the message with the given id.
     * @param messageId The id of the message to return.
     */
    fun getMessage(messageId: String): ComposeMessageListItemState? {
        return messageState.getMessage(messageId)
    }

    fun removeMessageByIndex(index: Int){
        messageState.removeMessageByIndex(index)
    }

    fun removeMessage(msg: ComposeMessageListItemState){
        if (msg.conversationId == roomId){
            messageState.removeMessage(msg)
        }
    }

    fun clearMessage(){
        messageState.clearMessage()
    }

    private fun getTranslationContent(message:ChatMessage):String{
        var translationContent = ""
        val translations = (message.body as ChatTextMessageBody).translations
        if (translations.size > 0){
            translationContent = translations[0].translationText
        }
        return translationContent
    }
}