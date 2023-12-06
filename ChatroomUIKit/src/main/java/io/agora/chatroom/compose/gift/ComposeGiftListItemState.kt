package io.agora.chatroom.compose.gift

import io.agora.chatroom.service.GiftEntityProtocol

sealed class ComposeGiftListItemState

data class ComposeGiftItemState(
    val gift: GiftEntityProtocol
): ComposeGiftListItemState()