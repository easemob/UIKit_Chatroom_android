package io.agora.chatroom.viewmodel.member

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.model.toUser
import io.agora.chatroom.service.ChatLog
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.UserOperationType
import io.agora.chatroom.UIChatroomService
import io.agora.chatroom.viewmodel.RequestListViewModel

open class MemberListViewModel(
    private val roomId: String,
    private val service: UIChatroomService,
    private val pageSize: Int = 10,
    private val atLeastShowingTime: Long = 1000L,
    private val showLabel:Boolean = false,
): RequestListViewModel<UserEntity>(atLeastShowingTime = atLeastShowingTime) {
    private var cursor: String? = null
    private var hasMore: Boolean = true

    private val _showLabel : MutableState<Boolean> = mutableStateOf(showLabel)
    var isShowLabel = _showLabel

    fun fetchRoomMembers(
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ){
        cursor = null
        hasMore = true
        clear()
        // clear cache data
        ChatroomUIKitClient.getInstance().getCacheManager().clearRoomUserCache()
        fetchRoomMembers(true, onSuccess = { list ->
            val owner = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomOwner?.userId ?: ""
            val contains = list.map {
                it.userId
            }.contains(owner)
            val newList = list.toMutableList()
            if (!contains) {
                newList.add(0, ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomOwner ?: UserEntity(owner))
            }
            add(newList)
            onSuccess.invoke(newList)
        }, onError = { code, message ->
            onError.invoke(code, message)
        })
    }

    fun fetchMoreRoomMembers(
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ){
        if (!hasMore) {
            onSuccess.invoke(emptyList())
            return
        }
        loadMore()
        fetchRoomMembers(false, onSuccess = { list ->
            addMore(list)
            onSuccess.invoke(list)
        }, onError = { code, message ->
            onError.invoke(code, message)
        })
    }

    private fun fetchRoomMembers(
        fetchUserInfo: Boolean = false,
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getChatService().fetchMembers(roomId, cursor, pageSize, {cursorResult ->
            hasMore = (cursorResult.data.size == pageSize) && !cursorResult.cursor.isNullOrBlank()
            cursor = cursorResult.cursor
            val memberList = cursorResult.data
            ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMemberList(roomId, memberList)
            val propertyList = memberList.filter { userId ->
                !ChatroomUIKitClient.getInstance().getCacheManager().inCache(userId)
            }
            if (fetchUserInfo && propertyList.isNotEmpty()) {
                fetchUsersInfo(propertyList, { list ->
                    val result = memberList.map { userId ->
                        ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                    }
                    onSuccess.invoke(result)
                }, { code, error ->
                    val result = memberList.map { userId ->
                        ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                    }
                    onSuccess.invoke(result)
                    ChatLog.e("fetchRoomMembers", "fetchUsersInfo error: $code, $error")
                })
            } else {
                val result = memberList.map { userId ->
                    ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                }
                onSuccess.invoke(result)
            }
        }, {code, error ->
            error(code, error)
            onError.invoke(code, error)
        })
    }

    /**
     * Returns the cached chatroom members.
     */
    fun getCacheMemberList(): List<UserEntity> {
        return ChatroomUIKitClient.getInstance().getCacheManager().getRoomMemberList(roomId).map { userId ->
            ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
        }
    }

    /**
     * Fetches the user information of the chatroom members.
     */
    fun fetchUsersInfo(
        userIdList: List<String>,
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getUserService().getUserInfoList(userIdList, { list ->
            val users = list.map {
                it.toUser()
            }
            users.forEach {
                ChatroomUIKitClient.getInstance().getCacheManager().saveUserInfo(it.userId, it)
            }
            refresh()
            onSuccess.invoke(users)
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    /**
     * Returns whether there are more members to fetch.
     */
    fun hasMore(): Boolean {
        return hasMore
    }

    /**
     * Fetches user information based on visible items on the page.
     */
    fun fetchUsersInfo(firstVisibleIndex: Int, lastVisibleIndex: Int) {
        items.subList(firstVisibleIndex, lastVisibleIndex).filter { user ->
            !ChatroomUIKitClient.getInstance().getCacheManager().inCache(user.userId)
        }.let { list ->
            if (list.isNotEmpty()) {
                fetchUsersInfo(list.map { it.userId })
            }
        }
    }

    /**
     * Mutes a user.
     */
    fun muteUser(
        userId: String,
        onSuccess: OnValueSuccess<UserEntity> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getChatService().operateUser(roomId, userId, UserOperationType.MUTE, { chatroom ->
            ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMuteList(roomId, listOf(userId))
            onSuccess.invoke(ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(userId))
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    /**
     * Unmutes a user.
     */
    fun unmuteUser(
        userId: String,
        onSuccess: OnValueSuccess<UserEntity> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getChatService().operateUser(roomId, userId, UserOperationType.UNMUTE, { chatroom ->
            ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMuteMember(roomId, userId)
            ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMemberList(roomId, listOf(userId))
            onSuccess.invoke(ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(userId))
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    /**
     * Kicks a user.
     */
    fun removeUser(
        userId: String,
        onSuccess: OnValueSuccess<UserEntity> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getChatService().operateUser(roomId, userId, UserOperationType.KICK, { chatroom ->
            ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMember(roomId, userId)
            ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMuteMember(roomId, userId)
            onSuccess.invoke(ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(userId))
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    fun searchUsers(
        keyword: String,
        isMute: Boolean = false,
        onSuccess: OnValueSuccess<List<UserEntity>> = {}
    ) {
        clear()
        if (keyword.isEmpty()) {
            onSuccess.invoke(emptyList())
            return
        }
        if (isMute) {
            ChatroomUIKitClient.getInstance().getCacheManager().getRoomMuteList(roomId).let { list ->
                val result = list.filter { userId ->
                    val user = ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                    if (user.nickname.isNullOrEmpty()) {
                        user.userId.contains(keyword)
                    } else {
                        if (!user.nickname!!.contains(keyword)) {
                            user.userId.contains(keyword)
                        }else {
                            true
                        }
                    }
                }.map { userId ->
                    ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                }
                add(result)
                onSuccess.invoke(result)
            }
        } else {
            ChatroomUIKitClient.getInstance().getCacheManager().getRoomMemberList(roomId).let { list ->
                val owner = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomOwner?.userId ?: ""
                val contains = list.contains(owner)
                val newList = list.toMutableList()
                if (!contains) {
                    newList.add(0,owner)
                }
                val result = newList.filter { userId ->
                    val user = ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                    if (user.nickname.isNullOrEmpty()) {
                        user.userId.contains(keyword)
                    } else {
                        if (!user.nickname!!.contains(keyword)) {
                            user.userId.contains(keyword)
                        }else {
                            true
                        }
                    }
                }.map { userId ->
                    ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                }
                add(result)
                onSuccess.invoke(result)
            }
        }

    }
}
