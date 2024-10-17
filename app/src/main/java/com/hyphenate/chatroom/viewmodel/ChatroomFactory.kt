package com.hyphenate.chatroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.chatroom.UIChatroomService
import com.hyphenate.chatroom.service.ChatroomUIKitClient

class ChatroomFactory(
    private val service: UIChatroomService,
    private val isDarkTheme: Boolean? = ChatroomUIKitClient.getInstance().getCurrentTheme(),
): ViewModelProvider.Factory {

    /**
     * The list of factories that can build [ViewModel]s that our Messages feature components use.
     */
    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        ChatroomViewModel::class.java to {
            ChatroomViewModel(
                service = service,
                isDarkTheme = isDarkTheme,
            )
        },
    )

    /**
     * Creates the required [ViewModel] for our use case, based on the [factories] we provided.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = factories[modelClass]?.invoke()
            ?: throw IllegalArgumentException(
                "MessagesViewModelFactory can only create instances of " +
                        "the following classes: ${factories.keys.joinToString { it.simpleName }}"
            )

        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}