package com.hyphenate.chatroom.compose.gift

import com.hyphenate.chatroom.service.GiftEntityProtocol

sealed class ComposeGiftListItemState

data class ComposeGiftItemState(
    val gift: GiftEntityProtocol
): ComposeGiftListItemState()