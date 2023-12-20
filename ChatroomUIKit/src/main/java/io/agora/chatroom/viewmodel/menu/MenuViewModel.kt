package io.agora.chatroom.viewmodel.menu

import android.content.Context
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.service.ChatMessage
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.uikit.R

open class MenuViewModel (
    isDarkTheme: Boolean? = false,
    isShowTitle:Boolean = false,
    isShowCancel:Boolean = true,
    title:String = "",
    menuList: List<UIComposeSheetItem> = emptyList(),
    isExpanded: Boolean = false,
): BottomSheetViewModel<UIComposeSheetItem>(isDarkTheme, isShowTitle, isShowCancel, title, isExpanded = isExpanded, contentList = menuList) {

    // The user-selected bean displayed on the menu
    private var selectedBean: Any? = null

    open fun setSelectedBean(bean: Any) {
        selectedBean = bean
    }

    fun getSelectedBean(): Any? {
        return selectedBean
    }

}

/**
 * The viewModel of the selected message menu.
 */
class MessageMenuViewModel(
    isShowTitle:Boolean = false,
    isShowCancel:Boolean = true,
    title:String = "",
    menuList: List<UIComposeSheetItem> = emptyList(),
    isExpanded: Boolean = false,
): MenuViewModel(
    isShowTitle = isShowTitle,
    isShowCancel = isShowCancel,
    title = title,
    menuList = menuList,
    isExpanded = isExpanded
) {

    override fun setSelectedBean(bean: Any) {
        super.setSelectedBean(bean)
        if (contentList.isEmpty() && bean is ChatMessage) {
            val context = ChatroomUIKitClient.getInstance().getContext().context ?: return
            val messageMenuList = mutableListOf<UIComposeSheetItem>()
            if (ChatroomUIKitClient.getInstance().getCacheManager().inMuteCache(bean.conversationId(), bean.from)) {
                messageMenuList.add(UIComposeSheetItem(id = R.id.action_menu_translate, title =  context.getString(R.string.menu_item_translate)))
                if(bean.from == ChatroomUIKitClient.getInstance().getCurrentUser().userId){
                    messageMenuList.add(UIComposeSheetItem(id = R.id.action_menu_recall, title =  context.getString(R.string.menu_item_recall)))
                }
                if (ChatroomUIKitClient.getInstance().isCurrentRoomOwner() && bean.from != ChatroomUIKitClient.getInstance().getCurrentUser().userId ){
                    messageMenuList.add(UIComposeSheetItem(id = R.id.action_menu_unmute, title =  context.getString(R.string.menu_item_unmute)))
                }
                messageMenuList.add(UIComposeSheetItem(id = R.id.action_menu_report, title =  context.getString(R.string.menu_item_report), isError = true))
            } else {
                messageMenuList.add(UIComposeSheetItem(id = R.id.action_menu_translate, title =  context.getString(R.string.menu_item_translate)))
                if(bean.from == ChatroomUIKitClient.getInstance().getCurrentUser().userId){
                    messageMenuList.add(UIComposeSheetItem(id = R.id.action_menu_recall, title =  context.getString(R.string.menu_item_recall)))
                }
                if (ChatroomUIKitClient.getInstance().isCurrentRoomOwner() && bean.from != ChatroomUIKitClient.getInstance().getCurrentUser().userId ){
                    messageMenuList.add(UIComposeSheetItem(id = R.id.action_menu_mute, title =  context.getString(R.string.menu_item_mute)))
                }
                messageMenuList.add(UIComposeSheetItem(id = R.id.action_menu_report, title =  context.getString(R.string.menu_item_report), isError = true))
            }
            clear()
            add(messageMenuList)
        }
    }
}

class RoomMemberMenuViewModel (
    isDarkTheme: Boolean? = false,
    isShowTitle:Boolean = false,
    isShowCancel:Boolean = true,
    title:String = "",
    menuList: List<UIComposeSheetItem> = emptyList(),
    isExpanded: Boolean = false,
    var user: UserEntity = UserEntity("")
): MenuViewModel(isDarkTheme, isShowTitle, isShowCancel, title, isExpanded = isExpanded, menuList = menuList) {

    /**
     * Set the menu list according to the tab.
     */
    fun setMenuList(context: Context, tab: String) {
        val memberMenuList = mutableListOf<UIComposeSheetItem>()
        val isMute = ChatroomUIKitClient.getInstance().getCacheManager().inMuteCache(
            ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomId,
            user.userId
        )
        when(tab){
            context.getString(R.string.member_management_participant) -> {
                if (isMute){
                    memberMenuList.add(UIComposeSheetItem(id = R.id.action_menu_unmute, title =  context.getString(R.string.menu_item_unmute)))
                }else{
                    memberMenuList.add(UIComposeSheetItem(id = R.id.action_menu_mute, title = context.getString(R.string.menu_item_mute)))
                }
                memberMenuList.add(UIComposeSheetItem(id = R.id.action_menu_remove, title =  context.getString(R.string.menu_item_remove), isError = true))
            }
            context.getString(R.string.member_management_mute) -> {
                memberMenuList.add(UIComposeSheetItem(id = R.id.action_menu_unmute, title =  context.getString(R.string.menu_item_unmute)))
                memberMenuList.add(UIComposeSheetItem(id = R.id.action_menu_remove, title =  context.getString(R.string.menu_item_remove), isError = true))
            }
        }
        clear()
        add(memberMenuList)
    }
}