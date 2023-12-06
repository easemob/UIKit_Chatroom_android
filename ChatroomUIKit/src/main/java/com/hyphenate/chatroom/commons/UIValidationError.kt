package com.hyphenate.chatroom.commons


/**
 * Represents a validation error for the user input.
 */
sealed class UIValidationError {
    /**
     * Represents a validation error that happens when the message length in the message input
     * exceed the maximum allowed message length.
     *
     * @param messageLength The current message length in the message input.
     * @param maxMessageLength The maximum allowed message length that we exceeded.
     */
     data class MessageLengthExceeded(
        val messageLength: Int,
        val maxMessageLength: Int,
    ) : UIValidationError()

}
