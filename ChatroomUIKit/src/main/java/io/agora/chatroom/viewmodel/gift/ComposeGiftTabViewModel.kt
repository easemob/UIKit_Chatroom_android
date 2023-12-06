package io.agora.chatroom.viewmodel.gift

import io.agora.chatroom.model.gift.AUIGiftTabInfo
import io.agora.chatroom.viewmodel.tab.TabWithVpViewModel

class ComposeGiftTabViewModel(
    giftTabInfo:List<AUIGiftTabInfo>,
): TabWithVpViewModel<AUIGiftTabInfo>(contentList = giftTabInfo) {

}