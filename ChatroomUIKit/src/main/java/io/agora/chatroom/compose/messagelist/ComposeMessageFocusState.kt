package io.agora.chatroom.compose.messagelist

/**
 * Represents the message focus state, in case the user jumps to a message.
 */
sealed class MessageFocusState

/**
 * Represents the state when the message is currently being focused.
 */
object MessageFocused : MessageFocusState() { override fun toString(): String = "MessageFocused" }

/**
 * Represents the state when we've removed the focus from the message.
 */
object MessageFocusRemoved : MessageFocusState() { override fun toString(): String = "MessageFocusRemoved" }
