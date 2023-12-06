package io.agora.chatroom.viewmodel.broadcast

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.agora.chatroom.UIChatroomService

class GlobalBroadcastViewModelFactory(
    private val content: MutableList<String> = mutableListOf(),
    private val context: Context,
    private val service: UIChatroomService,
) : ViewModelProvider.Factory {

    /**
     * The list of factories that can build [ViewModel]s that our Messages feature components use.
     */
    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        GlobalBroadcastViewModel::class.java to {
            GlobalBroadcastViewModel(
                content = content,
                service = service
            )
        }
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