package com.hyphenate.chatroom.viewmodel.gift

import com.hyphenate.chatroom.UIChatroomService
import com.hyphenate.chatroom.compose.gift.ComposeGiftItemState
import com.hyphenate.chatroom.compose.gift.ComposeGiftListItemState
import com.hyphenate.chatroom.service.ChatMessage
import com.hyphenate.chatroom.service.ChatroomUIKitClient
import com.hyphenate.chatroom.service.GiftEntityProtocol
import com.hyphenate.chatroom.service.GiftReceiveListener
import com.hyphenate.chatroom.viewmodel.ComposeBaseListViewModel

class ComposeGiftListViewModel(
    private val giftItems: List<ComposeGiftItemState> = emptyList(),
    private val service: UIChatroomService,
): ComposeBaseListViewModel<ComposeGiftListItemState>(
    contentList = giftItems
), GiftReceiveListener {

    fun registerGiftListener() {
        service.getGiftService().bindGiftListener(this)
    }

    private fun unregisterGiftListener() {
        service.getGiftService().unbindGiftListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        unregisterGiftListener()
    }

    override fun onGiftReceived(roomId: String, gift: GiftEntityProtocol?, message: ChatMessage) {
        super.onGiftReceived(roomId, gift, message)
        if (!ChatroomUIKitClient.getInstance().getUseGiftsInMsg()){
            gift?.let { giftEntity ->
                ChatroomUIKitClient.getInstance().parseUserInfo(message)?.let {
                    giftEntity.sendUser = it
                }
                addDateToIndex(data = ComposeGiftItemState(giftEntity))
            }
        }
    }

}