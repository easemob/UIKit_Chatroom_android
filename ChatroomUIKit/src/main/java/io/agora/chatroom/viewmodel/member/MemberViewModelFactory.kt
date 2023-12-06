package io.agora.chatroom.viewmodel.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.agora.chatroom.UIChatroomService

class MemberViewModelFactory(
    private val roomId: String,
    private val service: UIChatroomService,
    private val isRoomAdmin: Boolean,
    private val pageSize: Int = 10
): ViewModelProvider.Factory {

    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        MemberListViewModel::class.java to {
            MemberListViewModel(
                roomId = roomId,
                service = service,
                pageSize = pageSize
            )
        },
        MutedListViewModel::class.java to {
            MutedListViewModel(
                roomId = roomId,
                service = service,
                pageSize = pageSize
            )
        },
        MembersBottomSheetViewModel::class.java to {
            MembersBottomSheetViewModel(
                roomId = roomId,
                roomService = service,
                isAdmin = isRoomAdmin
            )
        }
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = factories[modelClass]?.invoke()
            ?: throw IllegalArgumentException(
                "MemberViewModelFactory can only create instances of " +
                        "the following classes: ${factories.keys.joinToString { it.simpleName }}"
            )

        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}