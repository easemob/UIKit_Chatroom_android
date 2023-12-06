package com.hyphenate.chatroom.viewmodel.member

import com.hyphenate.chatroom.UIChatroomService
import com.hyphenate.chatroom.viewmodel.menu.BottomSheetViewModel

data class MembersBottomSheetViewModel(
    val roomId: String,
    val roomService: UIChatroomService,
    val isAdmin: Boolean = false,
): BottomSheetViewModel<String>()