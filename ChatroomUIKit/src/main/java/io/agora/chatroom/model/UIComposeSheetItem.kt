package io.agora.chatroom.model


/**
 * The bean for the menu item.
 * @param index The index of the menu item.
 * @param id The id of the menu item.
 * @param title The title of the menu item.
 * @param isError Whether the menu item is an error item. It will have a specific color.
 */
data class UIComposeSheetItem(
    val id: Int,
    val title:String,
    val index: Int = 0,
    val isError: Boolean = false
)
