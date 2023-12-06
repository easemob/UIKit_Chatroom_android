package com.hyphenate.chatroom.viewmodel.gift

import com.hyphenate.chatroom.model.gift.AUIGiftTabInfo
import com.hyphenate.chatroom.viewmodel.menu.BottomSheetViewModel

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