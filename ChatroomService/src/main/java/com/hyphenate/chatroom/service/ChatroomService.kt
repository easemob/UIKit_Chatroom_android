package com.hyphenate.chatroom.service

enum class UserOperationType {
    /**
     * Add an admin.
     */
    ADD_ADMIN,

    /**
     * Remove an admin.
     */
    REMOVE_ADMIN,

    /**
     * Mute a user.
     */
    MUTE,

    /**
     * Unmute a user.
     */
    UNMUTE,

    /**
     * Block a user.
     */
    BLOCK,

    /**
     * Unblock a user.
     */
    UNBLOCK,

    /**
     * Kick a user.
     */
    KICK
}
interface ChatroomService: MessageHandleService {

    /**
     * Bind a listener to the chatroom.
     * @param listener The listener to bind.
     */
    fun bindListener(listener: ChatroomChangeListener)

    /**
     * Unbind a listener from the chatroom.
     * @param listener The listener to unbind.
     */
    fun unbindListener(listener: ChatroomChangeListener)

    /**
     * Join a chatroom.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     * @param onSuccess The callback to indicate the user joined the chatroom successfully.
     * @param onError The callback to indicate the user failed to join the chatroom.
     */
    fun joinChatroom(roomId: String,
                    userId: String,
                    onSuccess: OnValueSuccess<Chatroom>,
                    onError: OnError)

    /**
     * Leave a chatroom.
     * @param roomId The id of the chatroom
     */
    fun leaveChatroom(roomId: String,
                      userId: String,
                      onSuccess: OnSuccess,
                      onError: OnError)

    /**
     * destroy a chatroom.
     * @param roomId The id of the chatroom
     */
    fun destroyChatroom(roomId: String,
                      onSuccess: OnSuccess,
                      onError: OnError)

    /**
     * Fetch the members of the chatroom from server.
     * Note: Not include the owner and the admins.
     * @param roomId The id of the chatroom.
     * @param cursor The cursor position from which to start getting data. At the first call, if you set the cursor as null.
     * @param pageSize The number of members that you expect to get on each page. The value range is [1,50].
     * @param onSuccess The callback to indicate the members of the chatroom.
     * @param onError The callback to indicate the error.
     */
    fun fetchMembers(roomId: String,
                     cursor: String?,
                     pageSize: Int,
                     onSuccess: OnValueSuccess<ChatCursorResult<String>>,
                     onError: OnError)


    /**
     * Gets the list of muted chat room members from the server.
     * @param roomId The id of the chatroom.
     * @param pageNum The page number.
     * @param pageSize The number of members that you expect to get on each page. The value range is [1,50].
     * @param onSuccess The callback to indicate the members of the chatroom.
     * @param onError The callback to indicate the error.
     */
    fun fetchMuteList(roomId: String,
                      pageNum: Int,
                      pageSize: Int,
                      onSuccess: OnValueSuccess<Map<String, Long>>,
                      onError: OnError)

    /**
     * Get the announcement of the chatroom.
     * @param roomId The id of the chatroom.
     * @param onSuccess The callback to indicate the announcement of the chatroom.
     * @param onError The callback to indicate the error.
     */
    fun getAnnouncement(roomId: String,
                       onSuccess: OnValueSuccess<String?>,
                       onError: OnError)

    /**
     * Update the announcement of the chatroom.
     * @param roomId The id of the chatroom.
     * @param announcement The announcement of the chatroom.
     * @param onSuccess The callback to indicate the announcement of the chatroom.
     * @param onError The callback to indicate the error.
     */
    fun updateAnnouncement(roomId: String,
                           announcement: String,
                           onSuccess: OnSuccess,
                           onError: OnError)

    /**
     * Operate a user in the chatroom.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     * @param operation The operation to the user.
     * @param onSuccess The callback to indicate the announcement of the chatroom.
     * @param onError The callback to indicate the error.
     */
    fun operateUser(roomId: String,
                    userId: String,
                    operation: UserOperationType,
                    onSuccess: OnValueSuccess<Chatroom>,
                    onError: OnError)
}

interface ChatroomChangeListener: MessageListener {

    /**
     * Callback when the user joined the chatroom.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onUserJoined(roomId: String, userId: String){}

    /**
     * Callback when the user left the chatroom.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onUserLeft(roomId: String, userId: String){}

    /**
     * Callback when the user is kicked.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onUserBeKicked(roomId: String, userId: String){}

    /**
     * Callback when the user is muted.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onUserMuted(roomId: String, userId: String){}

    /**
     * Callback when the user is unmuted.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onUserUnmuted(roomId: String, userId: String){}

    /**
     * Callback when the user is been added to admins.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onAdminAdded(roomId: String, userId: String){}

    /**
     * Callback when the user is been removed from admins.
     * @param roomId The id of the chatroom.
     * @param userId The id of the user.
     */
    fun onAdminRemoved(roomId: String, userId: String){}

    /**
     * Callback when the announcement of the chatroom is updated.
     * @param roomId The id of the chatroom.
     * @param announcement The announcement of the chatroom.
     */
    fun onAnnouncementUpdated(roomId: String, announcement: String){}
}

interface ChatroomDestroyedListener{
    fun onRoomDestroyed(roomId: String, roomName: String)
}