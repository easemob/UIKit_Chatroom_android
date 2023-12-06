package io.agora.chatroom.viewmodel.member

import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.UIChatroomService

data class MutedListViewModel(
    private val roomId: String,
    private val service: UIChatroomService,
    private val pageSize: Int = 10
): MemberListViewModel(roomId, service, pageSize) {

    /**
     * Gets the mute list from cache.
     */
    fun getMuteList(onSuccess: OnValueSuccess<List<UserEntity>> = {}) {
        clear()
        loading()
        val muteList = ChatroomUIKitClient.getInstance().getCacheManager().getRoomMuteList(roomId)
        muteList.map { userId ->
            ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
        }.let {
            add(it)
            onSuccess.invoke(it)
        }
    }

    /**
     * Fetches the mute list from the server.
     */
    fun fetchMuteList(
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ) {
        clear()
        loading()
        service.getChatService().fetchMuteList(roomId, 1, 100, { muteList ->
            ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMuteList(roomId, muteList.map { it.key })
            val result = muteList.map { item ->
                ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(item.key)
            }
            add(result)
            onSuccess.invoke(result)
        }, { code, error ->
            error(code, error)
            onError.invoke(code, error)
        })
    }
}