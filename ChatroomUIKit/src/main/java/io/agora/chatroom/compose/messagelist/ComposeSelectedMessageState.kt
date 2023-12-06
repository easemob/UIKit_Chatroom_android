package io.agora.chatroom.compose.messagelist

import io.agora.chatroom.service.ChatMessage

/**
 * Represents a state when a message or its reactions were selected.
 *
 * @param message The selected message.
 */
public sealed class
ComposeSelectedMessageState(public val message: ChatMessage)

/**
 * Represents a state when a message was selected.
 */
public class SelectedMessageOptionsState(message: ChatMessage) :
    ComposeSelectedMessageState(message)

