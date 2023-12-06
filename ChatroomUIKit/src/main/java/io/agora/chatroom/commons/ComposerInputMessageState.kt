package io.agora.chatroom.commons

import io.agora.chatroom.model.UserInfoProtocol

/**
 * Represents the state within the message input.
 *
 * @param inputValue The current text value that's within the input.
 * @param currentUser The currently logged in user.
 */

data class ComposerInputMessageState(
    val inputValue: String = "",
    val ownCapabilities: Set<String> = setOf(),
    val validationErrors: List<UIValidationError> = emptyList(),
    val currentUser: UserInfoProtocol? = null,
)
