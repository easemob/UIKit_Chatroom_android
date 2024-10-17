package com.hyphenate.chatroom.service

/**
 * These events are used for callbacks after the user interacts with the Chat SDK interface.
 */
enum class ChatroomResultEvent {
    /**
     * The event is triggered when the current user joins a room.
     */
    JOIN_ROOM,

    /**
     * The event is triggered when the current user leaves a room.
     */
    LEAVE_ROOM,

    /**
     * The event is triggered when calls the chatroom announcement request.
     */
    ANNOUNCEMENT,

    /**
     * The event is triggered when the current user destroy a room.
     */
    DESTROY_ROOM,

    /**
     * The event is triggered when the current user kicks another room member out of a room.
     */
    KICK_MEMBER,

    /**
     * The event is triggered when the current user mutes another room member in a room.
     */
    MUTE_MEMBER,

    /**
     * The event is triggered when the current user unmutes another room member in a room.
     */
    UNMUTE_MEMBER,

    /**
     * The event is triggered when the current user translates a text message.
     */
    TRANSLATE,

    /**
     * The event is triggered when the current user reports a message.
     */
    REPORT,

    /**
     * The event is triggered when the current user fetches a room members from server.
     */
    FETCH_MEMBERS,

    /**
     * The event is triggered when the current user fetches a room mutes from server.
     */
    FETCH_MUTES,

    /**
     * The event is triggered when the current user sends a message.
     */
    SEND_MESSAGE,

    /**
     * The event is triggered when the current user deletes a message.
     */
    RECALL_MESSAGE
}

/**
 * This listener is used to call back the results of the user's interaction with the chat SDK interface.
 */
interface ChatroomResultListener {
    /**
     * Callback the results of the user's interaction with the chat SDK interface.
     * @param event The event of the user's interaction with the chat SDK interface.
     * @param errorCode The error code of the user's interaction with the chat SDK interface.
     * @param errorMessage The error message of the user's interaction with the chat SDK interface.
     */
    fun onEventResult(event: ChatroomResultEvent, errorCode: Int, errorMessage: String?)
}