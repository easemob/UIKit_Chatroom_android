package io.agora.chatroom.viewmodel.gift

import io.agora.chatroom.model.gift.AUIGiftTabInfo
import io.agora.chatroom.viewmodel.menu.BottomSheetViewModel

class ComposeGiftSheetViewModel(
    isDarkTheme: Boolean? = false,
    isShowTitle:Boolean = false,
    isShowCancel:Boolean = false,
    title:String = "",
    giftTabInfo:List<AUIGiftTabInfo>,
    isExpanded: Boolean = false,
) : BottomSheetViewModel<AUIGiftTabInfo>(
    isDarkTheme, isShowTitle, isShowCancel, title,
    isExpanded = isExpanded,
    contentList = giftTabInfo
){

}