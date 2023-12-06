package io.agora.chatroom.viewmodel.member

import io.agora.chatroom.UIChatroomService
import io.agora.chatroom.viewmodel.menu.BottomSheetViewModel

data class MembersBottomSheetViewModel(
    val roomId: String,
    val roomService: UIChatroomService,
    val isAdmin: Boolean = false,
): BottomSheetViewModel<String>()