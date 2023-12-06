package com.hyphenate.chatroom.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hyphenate.chatroom.ChatroomUIKitClient
import com.hyphenate.chatroom.UIChatroomService
import com.hyphenate.chatroom.service.ChatroomChangeListener

open class UIRoomViewModel(
    private val service: UIChatroomService,
    private val isDarkTheme:Boolean?,
) : ViewModel(), ChatroomChangeListener {

    private val _isShowLoading : MutableState<Boolean> = mutableStateOf(true)
    var isShowLoading = _isShowLoading

    private val _closeMemberSheet : MutableState<Boolean> = mutableStateOf(false)
    var closeMemberSheet = _closeMemberSheet

    private val _isShowBg : MutableState<Boolean> = mutableStateOf(true)
    var isShowBg = _isShowBg

    fun hideBg(){
        _isShowBg.value = false
    }

    val getTheme: Boolean
        get() = isDarkTheme == true

    val getRoomService: UIChatroomService
        get() = service

    fun registerChatroomChangeListener() {
        service.getChatService().bindListener(this)
    }

    private fun unRegisterChatroomChangeListener() {
        service.getChatService().unbindListener(this)
    }

    override fun onUserJoined(roomId: String, userId: String) {
        ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMemberList(roomId, arrayListOf(userId))
    }

    override fun onUserLeft(roomId: String, userId: String) {
        super.onUserLeft(roomId, userId)
        ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMember(roomId, userId)
    }

    override fun onUserMuted(roomId: String, userId: String) {
        super.onUserMuted(roomId, userId)
        ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMuteList(roomId, arrayListOf(userId))
        ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMember(roomId, userId)
    }

    override fun onUserUnmuted(roomId: String, userId: String) {
        super.onUserUnmuted(roomId, userId)
        ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMuteMember(roomId, userId)
        ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMemberList(roomId, arrayListOf(userId))
    }

    override fun onUserBeKicked(roomId: String, userId: String) {
        super.onUserBeKicked(roomId, userId)
        ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMember(roomId, userId)
    }

    override fun onCleared() {
        super.onCleared()
        unRegisterChatroomChangeListener()
        ChatroomUIKitClient.getInstance().clear()
    }
}