package io.agora.chatroom.viewmodel.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.model.UIComposeSheetItem

class MenuViewModelFactory(
    private val isDarkTheme: Boolean? = ChatroomUIKitClient.getInstance().getCurrentTheme(),
    private val title:String = "",
    private val menuList: List<UIComposeSheetItem> = emptyList(),
    private val isShowTitle:Boolean = true,
    private val isShowCancel:Boolean = true,
    ): ViewModelProvider.Factory {

    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        MenuViewModel::class.java to {
            MenuViewModel(
                isDarkTheme = isDarkTheme,
                title = title,
                menuList = menuList,
                isShowTitle = isShowTitle,
                isShowCancel = isShowCancel
            )
        },
        MessageMenuViewModel::class.java to {
            MessageMenuViewModel(
                isShowTitle = isShowTitle,
                isShowCancel = isShowCancel,
                title = title
            )
        },
        RoomMemberMenuViewModel::class.java to {
            RoomMemberMenuViewModel(
                isDarkTheme = isDarkTheme,
                title = title,
                menuList = menuList,
                isShowTitle = isShowTitle,
                isShowCancel = isShowCancel
            )
        }
    )

    /**
     * Creates the required [ViewModel] for our use case, based on the [factories] we provided.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = factories[modelClass]?.invoke()
            ?: throw IllegalArgumentException(
                "MenuViewModelFactory can only create instances of " +
                        "the following classes: ${factories.keys.joinToString { it.simpleName }}"
            )

        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}


