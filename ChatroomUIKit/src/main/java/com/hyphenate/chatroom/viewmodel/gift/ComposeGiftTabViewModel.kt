package com.hyphenate.chatroom.viewmodel.gift

import com.hyphenate.chatroom.model.gift.AUIGiftTabInfo
import com.hyphenate.chatroom.viewmodel.tab.TabWithVpViewModel

class ComposeGiftTabViewModel(
    giftTabInfo:List<AUIGiftTabInfo>,
): TabWithVpViewModel<AUIGiftTabInfo>(contentList = giftTabInfo) {

}